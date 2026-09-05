package gerenciacontato.model;

public class Contato {
    private Integer id;
    private String nome;
    private String telefone;
    private String email;

    public Contato(Integer id, String nome, String telefone, String email) {
        this.id = id;
        this.nome = nome;
        this.telefone = telefone;
        this.email = email;
    }

    public Contato(String nome, String telefone, String email) {
        this(null, nome, telefone, email);
    }
    
    public Contato(Integer id, String telefone, String email) {
        this.id = id;
        this.telefone = telefone;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
    
    public String getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString() {
        return "[" + id + "] " + nome + " | " + telefone + " | " + email;
    }
}
