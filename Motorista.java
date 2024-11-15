import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Motorista {
    private int id;
    private String nome;
    private String cpf;
    private String cnh;
    private String cidade;

    // Construtor
    public Motorista(int id, String nome, String cpf, String cnh, String cidade) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.cnh = cnh;
        this.cidade = cidade;
    }

    // Método para salvar o motorista no banco 
    public void salvar() throws SQLException {
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
    
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCnh() {
        return cnh;
    }

    public void setCnh(String cnh) {
        this.cnh = cnh;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }
}
