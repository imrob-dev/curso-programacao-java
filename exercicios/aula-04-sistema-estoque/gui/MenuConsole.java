package gui;

import model.Categoria;
import model.Produto;
import service.EstoqueService;

/**
 * Esta e a TELA do sistema em modo terminal: ela mostra os menus em loop,
 * le o que o usuario digita e pede ao SERVICE para fazer o trabalho.
 *
 * Repare no que NAO existe nesta classe: nenhuma regra de negocio. Ela nunca
 * decide se uma categoria pode ser excluida nem calcula o valor do estoque;
 * ela apenas pergunta isso ao service e mostra o resultado.
 *
 * No dia em que o sistema virar uma aplicacao desktop com Java Swing, e esta
 * classe que sera trocada por uma janela (JFrame). O service e o model
 * continuam exatamente como estao.
 *
 * Cada funcionalidade fica em um metodo separado (como pede o exercicio
 * sobre Metodos em Java).
 */
public class MenuConsole {

    /** O service que esta tela usa. Ele vem pronto de fora (do Main). */
    private final EstoqueService servico;

    /**
     * Cria a tela ja ligada a um service.
     *
     * Receber o service pelo construtor (em vez de criar um aqui dentro) e
     * importante: no Swing havera varias janelas, e todas precisam usar o
     * MESMO service, senao cada uma teria a sua propria copia dos dados.
     *
     * @param servico O service com as regras e os dados do estoque.
     */
    public MenuConsole(EstoqueService servico) {
        this.servico = servico;
    }

    /**
     * Mostra o menu principal em loop ate o usuario escolher "Sair".
     */
    public void iniciar() {
        boolean sair = false;
        while (!sair) {
            Console.limpar();
            Menu.menuPrincipal();
            int opcao = Console.lerOpcao();

            switch (opcao) {
                case 1:
                    menuProdutos();
                    break;
                case 2:
                    menuCategorias();
                    break;
                case 3:
                    menuRelatorios();
                    break;
                case 0:
                    sair = true;
                    Menu.sucesso("Saindo do sistema... Ate logo!");
                    break;
                default:
                    Menu.opcaoInvalida();
                    Console.pausar();
            }
        }
    }

    /**
     * Mostra o sub-menu de produtos em loop ate o usuario escolher "Voltar".
     */
    private void menuProdutos() {
        int opcao = -1;
        while (opcao != 0) {
            Console.limpar();
            Menu.menuProdutos();
            opcao = Console.lerOpcao();

            switch (opcao) {
                case 1:
                    inserirProduto();
                    break;
                case 2:
                    listarProdutos();
                    break;
                case 3:
                    alterarProduto();
                    break;
                case 4:
                    excluirProduto();
                    break;
                case 0:
                    break;
                default:
                    Menu.opcaoInvalida();
                    Console.pausar();
            }
        }
    }

    /**
     * CREATE - Cadastra um novo produto.
     */
    private void inserirProduto() {
        Console.limpar();
        Menu.titulo("Inserir Produto");

        if (!servico.podeCadastrarProduto()) {
            Menu.erro("Cadastre uma categoria antes de inserir produtos.");
            Console.pausar();
            return;
        }

        String nome = Console.lerTexto("Nome");
        double preco = Console.lerPrecoNaoNegativo("Preco");
        int quantidade = Console.lerInteiroNaoNegativo("Quantidade");

        System.out.println("\nEscolha a categoria:");
        TabelaPrinter.exibirCategorias(servico);
        int numero = Console.lerIndice("Numero da categoria", servico.getTotalCategorias());
        Categoria categoria = servico.buscarCategoria(numero - 1);

        servico.inserirProduto(nome, preco, quantidade, categoria);
        Menu.sucesso("Produto cadastrado com sucesso!");
        Console.pausar();
    }

    /**
     * READ - Lista todos os produtos.
     */
    private void listarProdutos() {
        Console.limpar();
        Menu.titulo("Lista de Produtos");
        TabelaPrinter.exibirProdutos(servico);
        Console.pausar();
    }

    /**
     * UPDATE - Altera um campo de um produto escolhido.
     */
    private void alterarProduto() {
        Console.limpar();
        Menu.titulo("Alterar Produto");

        if (servico.getTotalProdutos() == 0) {
            Menu.erro("Nenhum produto cadastrado.");
            Console.pausar();
            return;
        }

        TabelaPrinter.exibirProdutos(servico);
        int numero = Console.lerIndice("\nNumero do produto", servico.getTotalProdutos());
        int indice = numero - 1;

        Menu.menuCamposProduto();
        int campo = Console.lerOpcao();

        switch (campo) {
            case 1:
                servico.alterarNomeProduto(indice, Console.lerTexto("Novo nome"));
                Menu.sucesso("Produto atualizado com sucesso!");
                break;
            case 2:
                servico.alterarPrecoProduto(indice, Console.lerPrecoNaoNegativo("Novo preco"));
                Menu.sucesso("Produto atualizado com sucesso!");
                break;
            case 3:
                servico.alterarQuantidadeProduto(indice, Console.lerInteiroNaoNegativo("Nova quantidade"));
                Menu.sucesso("Produto atualizado com sucesso!");
                break;
            case 4:
                System.out.println("\nEscolha a nova categoria:");
                TabelaPrinter.exibirCategorias(servico);
                int numCat = Console.lerIndice("Numero da categoria", servico.getTotalCategorias());
                servico.alterarCategoriaProduto(indice, servico.buscarCategoria(numCat - 1));
                Menu.sucesso("Produto atualizado com sucesso!");
                break;
            default:
                Menu.opcaoInvalida();
        }
        Console.pausar();
    }

    /**
     * DELETE - Exclui um produto, pedindo confirmacao antes.
     */
    private void excluirProduto() {
        Console.limpar();
        Menu.titulo("Excluir Produto");

        if (servico.getTotalProdutos() == 0) {
            Menu.erro("Nenhum produto cadastrado.");
            Console.pausar();
            return;
        }

        TabelaPrinter.exibirProdutos(servico);
        int numero = Console.lerIndice("\nNumero do produto", servico.getTotalProdutos());

        if (Console.confirmar("Confirmar exclusao")) {
            servico.excluirProduto(numero - 1);
            Menu.sucesso("Produto excluido com sucesso!");
        } else {
            Menu.aviso("Exclusao cancelada.");
        }
        Console.pausar();
    }

    /**
     * Mostra o sub-menu de categorias em loop ate o usuario escolher "Voltar".
     */
    private void menuCategorias() {
        int opcao = -1;
        while (opcao != 0) {
            Console.limpar();
            Menu.menuCategorias();
            opcao = Console.lerOpcao();

            switch (opcao) {
                case 1:
                    inserirCategoria();
                    break;
                case 2:
                    listarCategorias();
                    break;
                case 3:
                    alterarCategoria();
                    break;
                case 4:
                    excluirCategoria();
                    break;
                case 0:
                    break;
                default:
                    Menu.opcaoInvalida();
                    Console.pausar();
            }
        }
    }

    /**
     * CREATE - Cadastra uma nova categoria.
     */
    private void inserirCategoria() {
        Console.limpar();
        Menu.titulo("Inserir Categoria");

        servico.inserirCategoria(Console.lerTexto("Nome"));
        Menu.sucesso("Categoria cadastrada com sucesso!");
        Console.pausar();
    }

    /**
     * READ - Lista todas as categorias.
     */
    private void listarCategorias() {
        Console.limpar();
        Menu.titulo("Lista de Categorias");
        TabelaPrinter.exibirCategorias(servico);
        Console.pausar();
    }

    /**
     * UPDATE - Altera o nome de uma categoria.
     */
    private void alterarCategoria() {
        Console.limpar();
        Menu.titulo("Alterar Categoria");

        if (servico.getTotalCategorias() == 0) {
            Menu.erro("Nenhuma categoria cadastrada.");
            Console.pausar();
            return;
        }

        TabelaPrinter.exibirCategorias(servico);
        int numero = Console.lerIndice("\nNumero da categoria", servico.getTotalCategorias());

        servico.alterarNomeCategoria(numero - 1, Console.lerTexto("Novo nome"));
        Menu.sucesso("Categoria atualizada com sucesso!");
        Console.pausar();
    }

    /**
     * DELETE - Exclui uma categoria, apenas se nao houver produtos vinculados.
     *
     * Quem decide se a exclusao e permitida e o SERVICE: aqui so perguntamos
     * (podeExcluirCategoria) e mostramos a mensagem certa.
     */
    private void excluirCategoria() {
        Console.limpar();
        Menu.titulo("Excluir Categoria");

        if (servico.getTotalCategorias() == 0) {
            Menu.erro("Nenhuma categoria cadastrada.");
            Console.pausar();
            return;
        }

        TabelaPrinter.exibirCategorias(servico);
        int numero = Console.lerIndice("\nNumero da categoria", servico.getTotalCategorias());

        if (!servico.podeExcluirCategoria(numero - 1)) {
            Menu.erro("Nao e possivel excluir: existem produtos nesta categoria.");
            Console.pausar();
            return;
        }

        if (Console.confirmar("Confirmar exclusao")) {
            servico.excluirCategoria(numero - 1);
            Menu.sucesso("Categoria excluida com sucesso!");
        } else {
            Menu.aviso("Exclusao cancelada.");
        }
        Console.pausar();
    }

    /**
     * Mostra o sub-menu de relatorios em loop ate o usuario escolher "Voltar".
     */
    private void menuRelatorios() {
        int opcao = -1;
        while (opcao != 0) {
            Console.limpar();
            Menu.menuRelatorios();
            opcao = Console.lerOpcao();

            switch (opcao) {
                case 1:
                    relatorioEstoque();
                    break;
                case 2:
                    relatorioPorCategoria();
                    break;
                case 0:
                    break;
                default:
                    Menu.opcaoInvalida();
                    Console.pausar();
            }
        }
    }

    /**
     * Relatorio geral do estoque, com o valor total por categoria.
     * Todos os numeros vem prontos do service: aqui so imprimimos.
     */
    private void relatorioEstoque() {
        Console.limpar();
        Menu.titulo("Relatorio de Estoque");

        if (servico.getTotalProdutos() == 0) {
            Menu.erro("Nenhum produto cadastrado.");
            Console.pausar();
            return;
        }

        System.out.println("Produtos cadastrados......: " + servico.getTotalProdutos());
        System.out.println("Total de unidades.........: " + servico.contarTotalUnidades());
        System.out.println("Valor total em estoque....: " + Menu.moeda(servico.calcularValorTotal()));
        System.out.println("Produtos com estoque baixo: " + servico.contarProdutosComEstoqueBaixo());

        System.out.println("\nValor por categoria:");
        for (Categoria c : servico.listarCategorias()) {
            System.out.println("- " + c.getNome()
                    + ": " + Menu.moeda(servico.calcularValorDaCategoria(c))
                    + " (" + servico.contarProdutosDaCategoria(c) + " produtos)");
        }

        Console.pausar();
    }

    /**
     * Relatorio que lista os produtos agrupados por categoria.
     */
    private void relatorioPorCategoria() {
        Console.limpar();
        Menu.titulo("Relatorio por Categoria");

        if (servico.getTotalCategorias() == 0) {
            Menu.erro("Nenhuma categoria cadastrada.");
            Console.pausar();
            return;
        }

        for (Categoria c : servico.listarCategorias()) {
            System.out.println("\nCategoria: " + c.getNome());

            for (Produto p : servico.produtosDaCategoria(c)) {
                System.out.println("- " + p.getNome()
                        + " | Qtde: " + p.getQuantidade()
                        + " | Valor: " + Menu.moeda(p.getValorTotal()));
            }

            System.out.println("Total na categoria: " + servico.contarUnidadesDaCategoria(c)
                    + " itens - " + Menu.moeda(servico.calcularValorDaCategoria(c)));
        }

        Console.pausar();
    }
}
