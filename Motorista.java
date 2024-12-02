import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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

    public boolean validaCpfDoMotorista(String cpf) {
        if (cpf.length() != 11) return false;
        if (cpf.matches("(\\d)\\1{10}")) return false; // Verifica dígitos repetidos

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }
        int primeiroDigitoVerificador = 11 - (soma % 11);
        if (primeiroDigitoVerificador >= 10) primeiroDigitoVerificador = 0;

        if (primeiroDigitoVerificador != Character.getNumericValue(cpf.charAt(9))) return false;

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }
        int segundoDigitoVerificador = 11 - (soma % 11);
        if (segundoDigitoVerificador >= 10) segundoDigitoVerificador = 0;

        return segundoDigitoVerificador == Character.getNumericValue(cpf.charAt(10));
    }

    public boolean validaDadosDoMotorista(String cpf) {
        if (!validaCpfDoMotorista(cpf)) {
            System.out.println("CPF inválido!");
            return false;
        }
        if (nome == null || nome.trim().isEmpty()) {
            System.out.println("Nome não pode estar vazio.");
            return false;
        }
        return true;
    }
    
    public static boolean cidadeExisteNoBanco(String cidade) throws SQLException {
        if (cidade == null || cidade.trim().isEmpty()) {
            System.out.println("Cidade inválida: valor nulo ou vazio.");
            return false;
        }
        String sql = "INSERT INTO cidade (cidade) VALUES (?)";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, cidade.trim());
            stmt.executeUpdate();
            System.out.println("Cidade inserida no banco com sucesso!");
            return true;
        }
    }

    public void salvarMotoristaNoBanco() throws SQLException {
        if (!validaDadosDoMotorista(cpf)) return;

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

    public static boolean motoristaTemViagemAndamento(int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM viagem WHERE motorista_id = ? AND status = 'Iniciada'";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id); // Corrigido para setInt
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public void atualizarMotoristaNoBanco() throws SQLException {
        if (!validaDadosDoMotorista(cpf)) return;

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

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getCnh() {
        return cnh;
    }

    public String getCidade() {
        return cidade;
    }


    public static void main(String[] args) {
        try {
            // Teste
            Motorista motorista = new Motorista(
                "Teste de funcionalidade",
                "86087638008",  //gerador de cpf online
                "CNH12345",
                "São Paulo"
            );

            motorista.salvarMotoristaNoBanco();

            List<Motorista> motoristas = listarMotoristas();
            for (Motorista m : motoristas) {
                System.out.println("ID: " + m.getId() +
                                   ", Nome: " + m.getNome() +
                                   ", CPF: " + m.getCpf() +
                                   ", CNH: " + m.getCnh() +
                                   ", Cidade: " + m.getCidade());
            }
        } catch (SQLException e) {
            System.out.println("Erro de banco de dados: " + e.getMessage());
        }
    }
}