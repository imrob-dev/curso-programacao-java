/**
 * Representa uma categoria de produtos do estoque.
 * Exemplos: "Eletronicos", "Perifericos".
 *
 * Aqui usamos uma classe simples (permitida no enunciado) apenas para
 * agrupar as informacoes de uma categoria em um unico lugar.
 */
public class Categoria {

    private String nome;

    /**
     * Cria uma nova categoria.
     *
     * @param nome Nome da categoria.
     */
    public Categoria(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
