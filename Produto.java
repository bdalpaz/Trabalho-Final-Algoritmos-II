import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Produto {
    private int id;
    private final String nome;
    private int quantidade;

    public Produto(int id, String nome, int quantidade) {
        this.id = id;
        this.nome = nome;
        this.quantidade = quantidade;
    }

    public Produto(String nome, int quantidade) {
        this(0, nome, quantidade);
    }

    public boolean validaDadosDoProduto() {
        if (nome == null || nome.trim().isEmpty()) {
            System.out.println("Nome do produto não pode estar vazio.");
            return false;
        }
        if (quantidade < 0) {
            System.out.println("Quantidade não pode ser negativa.");
            return false;
        }
        return true;
    }

    public static boolean produtoTemViagemAndamento(int id) throws SQLException {
        String sql = "SELECT COUNT(*) FROM viagem WHERE produto_id = ? and status = 'Iniciada'";
        try (Connection conn = PostgresConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public void salvarProdutoNoBanco() throws SQLException {
        if (!validaDadosDoProduto())
            return;

        String sql = "INSERT INTO produto (nome, quantidade) VALUES (?, ?)";
        try (Connection conn = PostgresConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, this.nome);
            stmt.setInt(2, this.quantidade);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    this.id = generatedKeys.getInt(1);
                    System.out.println("Produto salvo com sucesso! ID: " + this.id);
                }
            }
        }
    }

    public static List<Produto> listarProdutos() throws SQLException {
        String sql = "SELECT * FROM produto";
        List<Produto> produtos = new ArrayList<>();
        try (Connection conn = PostgresConnection.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                produtos.add(new Produto(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getInt("quantidade")));
            }
        }
        return produtos;
    }

    public void atualizarProdutoNoBanco() throws SQLException {
        if (!validaDadosDoProduto())
            return;

        String sql = "UPDATE produto SET nome = ?, quantidade = ? WHERE id = ?";
        try (Connection conn = PostgresConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, this.nome);
            stmt.setInt(2, this.quantidade);
            stmt.setInt(3, this.id);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Produto atualizado com sucesso!");
            } else {
                System.out.println("Produto não encontrado.");
            }
        }
    }

    public void excluirProdutoDoBanco() throws SQLException {
        String sql = "DELETE FROM produto WHERE id = ?";
        try (Connection conn = PostgresConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, this.id);
            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Produto excluído com sucesso!");
            } else {
                System.out.println("Produto não encontrado.");
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

    public int getQuantidade() {
        return quantidade;
    }

    @Override
    public String toString() {
        return "Produto [ID=" + id + ", Nome=" + nome + ", Quantidade=" + quantidade + "]";
    }

    // Esse Main aqui foi criado porque alguns erros estavam ocorrendo pela ausência
    // dele kkkk
    // Ele serve só pra fazer um teste rápido
    // Só ignorar
    public static void main(String[] args) {
        try {
            Produto produto = new Produto("Exemplo", 10);

            produto.salvarProdutoNoBanco();

            System.out.println("Produtos cadastrados no banco:");
            for (Produto p : listarProdutos()) {
                System.out.println(p);
            }

            produto.quantidade = 20;
            produto.atualizarProdutoNoBanco();

            if (produtoTemViagemAndamento(produto.getId())) {
                System.out.println("O produto possui viagens em andamento.");
            } else {
                System.out.println("O produto não possui viagens em andamento.");
            }

            produto.excluirProdutoDoBanco();

        } catch (SQLException e) {
            System.err.println("Erro ao acessar o banco de dados: " + e.getMessage());
        }
    }
}
