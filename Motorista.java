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

   final private int id;
   final private String nome;
   final private String cpf;
   final private String cnh;
   final private String cidade;

    public Motorista(int id, String nome, String cpf, String cnh, String cidade) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.cnh = cnh;
        this.cidade = cidade;
    }

    public boolean ValidaDadosDoMotorista() { // para conferir antes de ir para o banco
        if (nome == null || nome.trim().isEmpty()) {
            System.err.println("Nome não pode estar vazio");
            return false;
        }
        if (cpf == null || cpf.length() != 11) {
            System.err.println("CPF inválido!");
            return false;
        }
        return true;
    }  

    // Método para salvar o motorista no banco
    public void SalvarMotoristaNoBanco() throws SQLException {

        if (!ValidaDadosDoMotorista()) {
            return; // não salva se os dados forem inválidos
        }
        String sql = "INSERT INTO motorista (nome, cpf, cnh, cidade) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, this.nome);
            stmt.setString(2, this.cpf);
            stmt.setString(3, this.cnh);
            stmt.setString(4, this.cidade);
            
            stmt.executeUpdate();
            
            System.out.println("Motorista salvo com sucesso!");
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

    public static Motorista BuscarMotoristaPorId(int id) throws SQLException {
        String sql = "SELECT * FROM motorista WHERE id = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Motorista(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("cpf"),
                    rs.getString("cnh"),
                    rs.getString("cidade")
                );
            }
        }
        return null;
    }

    public void atualizarMotoristaNoBanco() throws SQLException {
        if (!ValidaDadosDoMotorista()) {
            return;
        }

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

    public void ExcluirMotoristaDoBanco() throws SQLException {
        String sql = "DELETE FROM motorista WHERE id = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Motorista excluído com sucesso!");
            } else {
                System.out.println("Motorista não encontrado");
            }
        }
    }

    // NÃO MEXAM AQUI
    public static class PostgresConnection {
        private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
        private static final String USER = "postgres"; 
        private static final String PASSWORD = "postgres"; 

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }

    // Método principal
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;

        while (continuar) {
            System.out.println("\n=== Motoristas: ===");
            System.out.println("1. Cadastrar Motorista");
            System.out.println("2. Listar Motoristas");
            System.out.println("3. Buscar Motorista por ID");
            System.out.println("4. Atualizar Motorista");
            System.out.println("5. Excluir Motorista");
            System.out.println("6. Sair");
            System.out.print("Escolha uma opção: ");

            int op = scanner.nextInt();
            scanner.nextLine();

            switch (op) {
                case 1:
                    CadastroDeMotorista(scanner);
                    break;
                case 2:
                    listarMotoristas(scanner);
                    break;
                case 3:
                    buscarMotorista(scanner);
                    break;
                case 4:
                    atualizarMotorista(scanner);
                    break;
                case 5:
                    excluirMotorista(scanner);
                    break;
                case 6:
                    continuar = false;
                    System.out.println("Saindo do sistema...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
                    break;
            }
        }
    }

    // Função para Cadastrar um Motorista
    private static void CadastroDeMotorista(Scanner scanner) {
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("CPF (11 dígitos): ");
        String cpf = scanner.nextLine();
        System.out.print("CNH: ");
        String cnh = scanner.nextLine();
        System.out.print("Cidade: ");
        String cidade = scanner.nextLine();

        Motorista motorista = new Motorista(0, nome, cpf, cnh, cidade);
        try {
            motorista.SalvarMotoristaNoBanco();
        } catch (SQLException e) {
            System.err.println("Erro ao salvar motorista: " + e.getMessage());
        }
    }

    // Função para listar motoristas
    private static void listarMotoristas(Scanner scanner) {
        try {
            List<Motorista> motoristas = Motorista.listarMotoristas();
            System.out.println("\n=== Lista de Motoristas ===");
            for (Motorista motorista : motoristas) {
                System.out.println("ID: " + motorista.id);
                System.out.println("Nome: " + motorista.nome);
                System.out.println("CPF: " + motorista.cpf);
                System.out.println("CNH: " + motorista.cnh);
                System.out.println("Cidade: " + motorista.cidade);
                System.out.println("--------------------------");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao listar motoristas: " + e.getMessage());
        }
    }

    // Função para Buscar Motorista por ID
    private static void buscarMotorista(Scanner scanner) {
        System.out.print("Digite o ID do motorista: ");
        int id = scanner.nextInt();

        try {
            Motorista motorista = Motorista.BuscarMotoristaPorId(id);
            if (motorista != null) {
                System.out.println("ID: " + motorista.id);
                System.out.println("Nome: " + motorista.nome);
                System.out.println("CPF: " + motorista.cpf);
                System.out.println("CNH: " + motorista.cnh);
                System.out.println("Cidade: " + motorista.cidade);
            } else {
                System.out.println("Motorista não encontrado.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar motorista: " + e.getMessage());
        }
    }

    // Atualiza na tabela
    private static void atualizarMotorista(Scanner scanner) {
        System.out.print("Digite o ID do motorista que deseja atualizar: ");
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

// pra excluir
    private static void excluirMotorista(Scanner scanner) {
        System.out.print("Digite o ID do motorista que deseja excluir: ");
        int id = scanner.nextInt();

        try {
            Motorista motorista = Motorista.BuscarMotoristaPorId(id);
            if (motorista != null) {
                motorista.ExcluirMotoristaDoBanco();
            } else {
                System.out.println("Motorista não encontrado.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir motorista: " + e.getMessage());
        }
    }
}
