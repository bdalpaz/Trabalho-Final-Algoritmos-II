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

    public void SalvarNoBanco() throws SQLException {
        String sql = "INSERT INTO cliente (id, nome, cpf, cidade) VALUES (?, ?, ?, ?)"; //Esses pontos de interrogação correspondem a resposta do usuário 

        try (Connection conn = PostgresConnection.getConnection();
            PreparedStatement stmtm = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

              stmtm.setInt(1, id);
              stmtm.setString(2, nome);
              stmtm.setString(3, cpf);
              stmtm.setString(4, cidade);
              
              stmtm.executeUpdate(); 

              ResultSet generatedKeys = stmtm.getGeneratedKeys();

              if (generatedKeys.next()) {
                this.id = generatedKeys.getInt(1); //associa o id do banco ao atributo id

            } else { 
     //         return; ta comentado pq ta dando erro
            }
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
