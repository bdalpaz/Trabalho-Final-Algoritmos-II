public class Cliente {

    public int id;
    public String nome;
    public String cpf;
    public String cidade;

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