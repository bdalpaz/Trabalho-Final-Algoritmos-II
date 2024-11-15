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

    public boolean ValidaDadosDoCliente() { // pra conferir antes de ir pro banco
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
