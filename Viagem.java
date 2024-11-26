import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Viagem {
    private int id;
    private String descricao;
    private String cidadeOrigem;
    private String cidadeDestino;
    private String status;
    private int motoristaId; // Relacionamento com Motorista
    private int clienteId; // Relacionamento com Cliente

    public Viagem(int id, String descricao, String cidadeOrigem, String cidadeDestino, String status, int motoristaId, int clienteId) {
        this.id = id;
        this.descricao = descricao;
        this.cidadeOrigem = cidadeOrigem;
        this.cidadeDestino = cidadeDestino;
        this.status = status;
        this.motoristaId = motoristaId;
        this.clienteId = clienteId;
    }

    public Viagem(String descricao, String cidadeOrigem, String cidadeDestino, String status, int motoristaId, int clienteId) {
        this(0, descricao, cidadeOrigem, cidadeDestino, status, motoristaId, clienteId);
    }

    public boolean validaDadosDaViagem() {
        if (descricao == null || descricao.trim().isEmpty()) {
            System.out.println("Descrição não pode estar vazia.");
            return false;
        }
        if (cidadeOrigem == null || cidadeOrigem.trim().isEmpty()) {
            System.out.println("Cidade de origem não pode estar vazia.");
            return false;
        }
        if (cidadeDestino == null || cidadeDestino.trim().isEmpty()) {
            System.out.println("Cidade de destino não pode estar vazia.");
            return false;
        }
        if (!status.equalsIgnoreCase("Iniciada") && !status.equalsIgnoreCase("Finalizada")) {
            System.out.println("Status inválido. Use 'Iniciada' ou 'Finalizada'.");
            return false;
        }
        return true;
    }

    public void salvarViagemNoBanco() throws SQLException {
        if (!validaDadosDaViagem()) return;

        String sql = "INSERT INTO viagem (descricao, cidade_origem, cidade_destino, status, motorista_id, cliente_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, this.descricao);
            stmt.setString(2, this.cidadeOrigem);
            stmt.setString(3, this.cidadeDestino);
            stmt.setString(4, this.status);
            stmt.setInt(5, this.motoristaId);
            stmt.setInt(6, this.clienteId);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    this.id = generatedKeys.getInt(1);
                    System.out.println("Viagem salva com sucesso! ID: " + this.id);
                }
            }
        }
    }

    public static List<Viagem> listarViagens() throws SQLException {
        String sql = "SELECT * FROM viagem";
        List<Viagem> viagens = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                viagens.add(new Viagem(
                        rs.getInt("id"),
                        rs.getString("descricao"),
                        rs.getString("cidade_origem"),
                        rs.getString("cidade_destino"),
                        rs.getString("status"),
                        rs.getInt("motorista_id"),
                        rs.getInt("cliente_id")
                ));
            }
        }
        return viagens;
    }

    public void atualizarViagemNoBanco() throws SQLException {
        if (!validaDadosDaViagem()) return;

        String sql = "UPDATE viagem SET descricao = ?, cidade_origem = ?, cidade_destino = ?, status = ?, motorista_id = ?, cliente_id = ? WHERE id = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, this.descricao);
            stmt.setString(2, this.cidadeOrigem);
            stmt.setString(3, this.cidadeDestino);
            stmt.setString(4, this.status);
            stmt.setInt(5, this.motoristaId);
            stmt.setInt(6, this.clienteId);
            stmt.setInt(7, this.id);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Viagem atualizada com sucesso!");
            } else {
                System.out.println("Viagem não encontrada.");
            }
        }
    }

    public void excluirViagemDoBanco() throws SQLException {
        String sql = "DELETE FROM viagem WHERE id = ?";
        try (Connection conn = PostgresConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Viagem excluída com sucesso!");
            } else {
                System.out.println("Viagem não encontrada.");
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

    public String getDescricao() {
        return descricao;
    }

    public String getCidadeOrigem() {
        return cidadeOrigem;
    }

    public String getCidadeDestino() {
        return cidadeDestino;
    }

    public String getStatus() {
        return status;
    }

    public int getMotoristaId() {
        return motoristaId;
    }

    public int getClienteId() {
        return clienteId;
    }

    @Override
    public String toString() {
        return "Viagem [ID=" + id + ", Descrição=" + descricao + ", Cidade Origem=" + cidadeOrigem +
               ", Cidade Destino=" + cidadeDestino + ", Status=" + status + ", Motorista ID=" + motoristaId +
               ", Cliente ID=" + clienteId + "]";
    }
}
