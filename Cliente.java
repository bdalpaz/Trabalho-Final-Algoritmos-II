import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Cliente {

    public int id;
    public String nome;
    public String cpf;
    public String cidade;

    public Cliente(int id, String nome, String cpf, String cidade) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.cidade = cidade;
    }


    public Cliente(String nome, String cpf, String cidade) {
        this(0, nome, cpf, cidade);
    }


// Teste pra ver se pega melhor as respostas 
//não mexer
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }


    public boolean validaCpfDoCliente(String cpf){
        if (cpf.length() != 11) return false;
        if (cpf.matches("(\\d)\1{10}")) return false;
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
    public boolean validaDadosDoCliente() {
        if (nome == null || nome.trim().isEmpty()) {
            System.out.println("Nome não pode estar vazio.");
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

    public static boolean clienteTemViagemAndamento(int id) throws SQLException {
        
        String sql = "SELECT COUNT(*) FROM viagem WHERE cliente_id = ? and status = 'Iniciada'";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public void salvarClienteNoBanco() throws SQLException {
        if (!validaDadosDoCliente()) {
            return; // Não salva se os dados forem inválidos
        }

        String sql = "INSERT INTO cliente (nome, cpf, cidade) VALUES (?, ?, ?)";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nome);
            stmt.setString(2, cpf);
            stmt.setString(3, cidade);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    this.id = generatedKeys.getInt(1);
                    System.out.println("Cliente salvo com sucesso! ID: " + this.id);
                }
            }
        }
    }

    public static List<Cliente> listarClientes() throws SQLException {
        String sql = "SELECT * FROM cliente";
        List<Cliente> clientes = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                clientes.add(new Cliente(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("cpf"),
                        rs.getString("cidade")
                ));
            }
        }
        return clientes;
    }

    public void atualizarClienteNoBanco() throws SQLException {
        if (!validaDadosDoCliente()) {
            return; // Não atualiza se os dados forem inválidos
        }

        String sql = "UPDATE cliente SET nome = ?, cpf = ?, cidade = ? WHERE id = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, this.nome);
            stmt.setString(2, this.cpf);
            stmt.setString(3, this.cidade);
            stmt.setInt(4, this.id);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Cliente atualizado com sucesso!");
            } else {
                System.out.println("Cliente não encontrado.");
            }
        }
    }

    public void excluirClienteDoBanco() throws SQLException {
        String sql = "DELETE FROM cliente WHERE id = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Cliente excluído com sucesso!");
            } else {
                System.out.println("Cliente não encontrado.");
            }
        }
    }

    // Conexão com o banco 
    public static class PostgresConnection {
        private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
        private static final String USER = "postgres";
        private static final String PASSWORD = "postgres";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }
}

