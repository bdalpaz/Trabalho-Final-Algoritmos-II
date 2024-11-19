import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Motorista {

    private int id;
    private final String nome;
    private final String cpf;
    private final String cnh;
    private final String cidade;

    public Motorista(int id, String nome, String cpf, String cnh, String cidade) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.cnh = cnh;
        this.cidade = cidade;
    }

    public Motorista(String nome, String cpf, String cnh, String cidade) {
        this(0, nome, cpf, cnh, cidade);
    }

    public boolean validaDadosDoMotorista() {
        if (nome == null || nome.trim().isEmpty()) {
            System.out.println("Nome não pode estar vazio.");
            return false;
        }
        if (cpf == null || cpf.length() != 11) {
            System.out.println("CPF inválido!");
            return false;
        }
        try {
            if (!cidadeExisteNoBanco(this.cidade)) {
                System.out.println("Cidade informada não existe no banco de dados!");
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao verificar cidade no banco de dados: " + e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean cidadeExisteNoBanco(String cidade) throws SQLException {
        if (cidade == null || cidade.trim().isEmpty()) {
            System.out.println("Cidade inválida: valor nulo ou vazio.");
            return false;
        }
    
        String sql = "SELECT COUNT(*) FROM cidade WHERE cidadeibge = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cidade.trim());
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    public void salvarMotoristaNoBanco() throws SQLException {
        if (!validaDadosDoMotorista()) return;

        String sql = "INSERT INTO motorista (nome, cpf, cnh, cidade) VALUES (?, ?, ?, ?)";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, this.nome);
            stmt.setString(2, this.cpf);
            stmt.setString(3, this.cnh);
            stmt.setString(4, this.cidade);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    this.id = generatedKeys.getInt(1);
                    System.out.println("Motorista salvo com sucesso! ID: " + this.id);
                }
            }
        }
    }

    public static List<Motorista> listarMotoristas() throws SQLException {
        String sql = "SELECT * FROM motorista";
        List<Motorista> motoristas = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                motoristas.add(new Motorista(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("cnh"),
                        rs.getString("cidade")
                ));
            }
        }
        return motoristas;
    }

    public void atualizarMotoristaNoBanco() throws SQLException {
        if (!validaDadosDoMotorista()) return;

        String sql = "UPDATE motorista SET nome = ?, cpf = ?, cnh = ?, cidade = ? WHERE id = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, this.nome);
            stmt.setString(2, this.cpf);
            stmt.setString(3, this.cnh);
            stmt.setString(4, this.cidade);
            stmt.setInt(5, this.id);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Motorista atualizado com sucesso!");
            } else {
                System.out.println("Motorista não encontrado.");
            }
        }
    }

    public void excluirMotoristaDoBanco() throws SQLException {
        String sql = "DELETE FROM motorista WHERE id = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Motorista excluído com sucesso!");
            } else {
                System.out.println("Motorista não encontrado.");
            }
        }
    }

    public static class PostgresConnection {
        private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
        private static final String USER = "postgres";
        private static final String PASSWORD = "postgres";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n=== Motoristas ===");
            System.out.println("1. Cadastrar Motorista");
            System.out.println("2. Listar Motoristas");
            System.out.println("3. Atualizar Motorista");
            System.out.println("4. Excluir Motorista");
            System.out.println("5. Sair");
            System.out.print("Escolha uma opção: ");

            int op = scanner.nextInt();
            scanner.nextLine();

            switch (op) {
                case 1 -> cadastrarMotorista(scanner);
                case 2 -> listarMotoristasMenu();
                case 3 -> atualizarMotorista(scanner);
                case 4 -> excluirMotorista(scanner);
                case 5 -> {
                    continuar = false;
                    System.out.println("Saindo do sistema...");
                }
                default -> System.out.println("Opção inválida! Tente novamente.");
            }
        }
    }

    private static void cadastrarMotorista(Scanner scanner) {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("CPF (11 dígitos): ");
        String cpf = scanner.nextLine();
        System.out.print("CNH: ");
        String cnh = scanner.nextLine();
        System.out.print("Cidade: ");
        String cidade = scanner.nextLine();

        Motorista motorista = new Motorista(nome, cpf, cnh, cidade);
        try {
            motorista.salvarMotoristaNoBanco();
        } catch (SQLException e) {
            System.err.println("Erro ao salvar motorista: " + e.getMessage());
        }
    }

    private static void listarMotoristasMenu() {
        try {
            List<Motorista> motoristas = Motorista.listarMotoristas();
            if (motoristas.isEmpty()) {
                System.out.println("Nenhum motorista encontrado.");
            } else {
                System.out.println("\n=== Lista de Motoristas ===");
                for (Motorista motorista : motoristas) {
                    System.out.printf("ID: %d\nNome: %s\nCPF: %s\nCNH: %s\nCidade: %s\n",
                            motorista.id, motorista.nome, motorista.cpf, motorista.cnh, motorista.cidade);
                    System.out.println("--------------------------");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar motoristas: " + e.getMessage());
        }
    }

    private static void atualizarMotorista(Scanner scanner) {
        System.out.print("Digite o ID do motorista a ser atualizado: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Novo Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Novo CPF (11 dígitos): ");
        String cpf = scanner.nextLine();
        System.out.print("Nova CNH: ");
        String cnh = scanner.nextLine();
        System.out.print("Nova Cidade: ");
        String cidade = scanner.nextLine();

        Motorista motorista = new Motorista(id, nome, cpf, cnh, cidade);
        try {
            motorista.atualizarMotoristaNoBanco();
        } catch (SQLException e) {
            System.err.println("Erro ao atualizar motorista: " + e.getMessage());
        }
    }

    private static void excluirMotorista(Scanner scanner) {
        System.out.print("Digite o ID do motorista a ser excluído: ");
        int id = scanner.nextInt();

        try {
            Motorista motorista = new Motorista(id, null, null, null, null);
            motorista.excluirMotoristaDoBanco();
        } catch (SQLException e) {
            System.err.println("Erro ao excluir motorista: " + e.getMessage());
        }
    }
}
