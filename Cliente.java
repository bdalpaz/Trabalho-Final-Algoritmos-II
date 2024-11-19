import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


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

    public boolean ValidaDadosDoCliente() throws SQLException { // Adicione throws SQLException
      if (nome == null || nome.trim().isEmpty()) {
          System.out.println("Nome não pode estar vazio");
          return false;
      }
      if (cpf == null || cpf.length() != 11) {
          System.out.println("CPF inválido!");
          return false;
      }
      if (!cidadeExisteNoBanco(this.cidade)) { // Agora não gerará erro
          System.out.println("Cidade informada não existe no banco de dados!");
          return false;
      }
      return true;
  }
  
public static boolean cidadeExisteNoBanco(String cidade) throws SQLException {
  String sql = "SELECT COUNT(*) FROM cidade WHERE cidadeibge = ?";
  try (Connection conn = PostgresConnection.getConnection();
       PreparedStatement stmt = conn.prepareStatement(sql)) {
      stmt.setString(1, cidade);
      ResultSet rs = stmt.executeQuery();
      if (rs.next()) {
          return rs.getInt(1) > 0; // da true se encontrar a cidade
      }
  }
  return false;
}

    public void SalvarClienteNoBanco() throws SQLException {
      if (!ValidaDadosDoCliente()) {
        return; // não salva se os dados forem inválidos
    }
        String sql = "INSERT INTO cliente (nome, cpf, cidade) VALUES (?, ?, ?)"; //Esses pontos de interrogação correspondem a resposta do usuário 

        try (Connection conn = PostgresConnection.getConnection();
    PreparedStatement stmtm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

    stmtm.setString(1, nome);
    stmtm.setString(2, cpf);
    stmtm.setString(3, cidade);

    stmtm.executeUpdate();

    ResultSet generatedKeys = stmtm.getGeneratedKeys();
    if (generatedKeys.next()) {
        this.id = generatedKeys.getInt(1); /*Como o id do cliente ta sendo criado no banco automaticamente
        essa linha ai serve pra recuparar ele*/
    } else {
        System.out.println("Falha ao obter o ID gerado para o cliente."); //só pra ser mais vísivel se der erro
    }
} catch (SQLException e) {
    System.err.println("Erro ao salvar o cliente no banco: " + e.getMessage());// eu espero nunca ver essa mensagem
}
    }

    

  public String getNome(){
    return nome;

  }

  public String getCpf(){
    return cpf;

  }
  public String getCidade(){
    return cidade;

  }

  public void setNome(String nomeAtributo) {
    this.nome = nomeAtributo;
  }

  public void setCpf(String cpfAtributo) {
    this.cpf = cpfAtributo;
  }

  public void setCidade(String cidadeAtributo) {
    this.cidade = cidadeAtributo;
  }



    
  }  
