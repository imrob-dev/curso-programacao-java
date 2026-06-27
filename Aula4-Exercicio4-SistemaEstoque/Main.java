/**
 * Sistema de Estoque com CRUD (Exercicio 4 - Aula 4 / JAVA-104).
 *
 * Esta classe e o "centro" do programa: ela mostra os menus em loop e chama
 * os metodos certos para cada operacao. Cada funcionalidade fica em um metodo
 * separado (como pede o exercicio sobre Metodos em Java).
 *
 * Organizacao do projeto:
 *   - Produto / Categoria : as entidades (os dados).
 *   - Estoque             : guarda os dados e faz inserir/buscar/remover.
 *   - Console             : le e valida tudo que o usuario digita.
 *   - Menu                : textos coloridos (menus e mensagens).
 *   - TabelaPrinter       : exibe os dados em tabelas coloridas.
 *   - Main                : junta tudo e controla o fluxo.
 */
public class Main {

    private static final Estoque estoque = new Estoque();

    public static void main(String[] args) {
        carregarDadosExemplo();

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
    private static void menuProdutos() {
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
    private static void inserirProduto() {
        Console.limpar();
        Menu.titulo("Inserir Produto");

        if (estoque.getTotalCategorias() == 0) {
            Menu.erro("Cadastre uma categoria antes de inserir produtos.");
            Console.pausar();
            return;
        }

        String nome = Console.lerTexto("Nome");
        double preco = Console.lerPrecoNaoNegativo("Preco");
        int quantidade = Console.lerInteiroNaoNegativo("Quantidade");

        System.out.println("\nEscolha a categoria:");
        TabelaPrinter.exibirCategorias(estoque);
        int numero = Console.lerIndice("Numero da categoria", estoque.getTotalCategorias());
        Categoria categoria = estoque.getCategoria(numero - 1);

        Produto produto = new Produto(nome, preco, quantidade, categoria);
        if (estoque.adicionarProduto(produto)) {
            Menu.sucesso("Produto cadastrado com sucesso!");
        } else {
            Menu.erro("Nao foi possivel cadastrar: estoque cheio.");
        }
        Console.pausar();
    }

    /**
     * READ - Lista todos os produtos.
     */
    private static void listarProdutos() {
        Console.limpar();
        Menu.titulo("Lista de Produtos");
        TabelaPrinter.exibirProdutos(estoque);
        Console.pausar();
    }

    /**
     * UPDATE - Altera um campo de um produto escolhido.
     */
    private static void alterarProduto() {
        Console.limpar();
        Menu.titulo("Alterar Produto");

        if (estoque.getTotalProdutos() == 0) {
            Menu.erro("Nenhum produto cadastrado.");
            Console.pausar();
            return;
        }

        TabelaPrinter.exibirProdutos(estoque);
        int numero = Console.lerIndice("\nNumero do produto", estoque.getTotalProdutos());
        Produto produto = estoque.getProduto(numero - 1);

        System.out.println("\nO que deseja alterar?");
        System.out.println("[ 1 ] - Nome");
        System.out.println("[ 2 ] - Preco");
        System.out.println("[ 3 ] - Quantidade");
        System.out.println("[ 4 ] - Categoria");
        int campo = Console.lerOpcao();

        switch (campo) {
            case 1:
                produto.setNome(Console.lerTexto("Novo nome"));
                Menu.sucesso("Produto atualizado com sucesso!");
                break;
            case 2:
                produto.setPreco(Console.lerPrecoNaoNegativo("Novo preco"));
                Menu.sucesso("Produto atualizado com sucesso!");
                break;
            case 3:
                produto.setQuantidade(Console.lerInteiroNaoNegativo("Nova quantidade"));
                Menu.sucesso("Produto atualizado com sucesso!");
                break;
            case 4:
                System.out.println("\nEscolha a nova categoria:");
                TabelaPrinter.exibirCategorias(estoque);
                int numCat = Console.lerIndice("Numero da categoria", estoque.getTotalCategorias());
                produto.setCategoria(estoque.getCategoria(numCat - 1));
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
    private static void excluirProduto() {
        Console.limpar();
        Menu.titulo("Excluir Produto");

        if (estoque.getTotalProdutos() == 0) {
            Menu.erro("Nenhum produto cadastrado.");
            Console.pausar();
            return;
        }

        TabelaPrinter.exibirProdutos(estoque);
        int numero = Console.lerIndice("\nNumero do produto", estoque.getTotalProdutos());

        if (Console.confirmar("Confirmar exclusao")) {
            estoque.removerProduto(numero - 1);
            Menu.sucesso("Produto excluido com sucesso!");
        } else {
            Menu.aviso("Exclusao cancelada.");
        }
        Console.pausar();
    }

    /**
     * Mostra o sub-menu de categorias em loop ate o usuario escolher "Voltar".
     */
    private static void menuCategorias() {
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
    private static void inserirCategoria() {
        Console.limpar();
        Menu.titulo("Inserir Categoria");

        String nome = Console.lerTexto("Nome");
        Categoria categoria = new Categoria(nome);
        if (estoque.adicionarCategoria(categoria)) {
            Menu.sucesso("Categoria cadastrada com sucesso!");
        } else {
            Menu.erro("Nao foi possivel cadastrar: limite atingido.");
        }
        Console.pausar();
    }

    /**
     * READ - Lista todas as categorias.
     */
    private static void listarCategorias() {
        Console.limpar();
        Menu.titulo("Lista de Categorias");
        TabelaPrinter.exibirCategorias(estoque);
        Console.pausar();
    }

    /**
     * UPDATE - Altera o nome de uma categoria.
     */
    private static void alterarCategoria() {
        Console.limpar();
        Menu.titulo("Alterar Categoria");

        if (estoque.getTotalCategorias() == 0) {
            Menu.erro("Nenhuma categoria cadastrada.");
            Console.pausar();
            return;
        }

        TabelaPrinter.exibirCategorias(estoque);
        int numero = Console.lerIndice("\nNumero da categoria", estoque.getTotalCategorias());
        Categoria categoria = estoque.getCategoria(numero - 1);

        categoria.setNome(Console.lerTexto("Novo nome"));
        Menu.sucesso("Categoria atualizada com sucesso!");
        Console.pausar();
    }

    /**
     * DELETE - Exclui uma categoria, apenas se nao houver produtos vinculados.
     */
    private static void excluirCategoria() {
        Console.limpar();
        Menu.titulo("Excluir Categoria");

        if (estoque.getTotalCategorias() == 0) {
            Menu.erro("Nenhuma categoria cadastrada.");
            Console.pausar();
            return;
        }

        TabelaPrinter.exibirCategorias(estoque);
        int numero = Console.lerIndice("\nNumero da categoria", estoque.getTotalCategorias());
        Categoria categoria = estoque.getCategoria(numero - 1);

        if (estoque.contarProdutosDaCategoria(categoria) > 0) {
            Menu.erro("Nao e possivel excluir: existem produtos nesta categoria.");
            Console.pausar();
            return;
        }

        if (Console.confirmar("Confirmar exclusao")) {
            estoque.removerCategoria(numero - 1);
            Menu.sucesso("Categoria excluida com sucesso!");
        } else {
            Menu.aviso("Exclusao cancelada.");
        }
        Console.pausar();
    }

    /**
     * Mostra o sub-menu de relatorios em loop ate o usuario escolher "Voltar".
     */
    private static void menuRelatorios() {
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
     */
    private static void relatorioEstoque() {
        Console.limpar();
        Menu.titulo("Relatorio de Estoque");

        if (estoque.getTotalProdutos() == 0) {
            Menu.erro("Nenhum produto cadastrado.");
            Console.pausar();
            return;
        }

        int totalUnidades = 0;
        double valorTotal = 0;
        int estoqueBaixo = 0;

        for (int i = 0; i < estoque.getTotalProdutos(); i++) {
            Produto p = estoque.getProduto(i);
            totalUnidades += p.getQuantidade();
            valorTotal += p.getValorTotal();
            if (p.getQuantidade() < Estoque.ESTOQUE_MINIMO) {
                estoqueBaixo++;
            }
        }

        System.out.println("Produtos cadastrados......: " + estoque.getTotalProdutos());
        System.out.println("Total de unidades.........: " + totalUnidades);
        System.out.println("Valor total em estoque....: " + Menu.moeda(valorTotal));
        System.out.println("Produtos com estoque baixo: " + estoqueBaixo);

        System.out.println("\nValor por categoria:");
        for (int i = 0; i < estoque.getTotalCategorias(); i++) {
            Categoria c = estoque.getCategoria(i);
            double valorCategoria = 0;
            int qtdProdutos = 0;
            for (int j = 0; j < estoque.getTotalProdutos(); j++) {
                Produto p = estoque.getProduto(j);
                if (p.getCategoria() == c) {
                    valorCategoria += p.getValorTotal();
                    qtdProdutos++;
                }
            }
            System.out.println("- " + c.getNome() + ": " + Menu.moeda(valorCategoria)
                    + " (" + qtdProdutos + " produtos)");
        }

        Console.pausar();
    }

    /**
     * Relatorio que lista os produtos agrupados por categoria.
     */
    private static void relatorioPorCategoria() {
        Console.limpar();
        Menu.titulo("Relatorio por Categoria");

        if (estoque.getTotalCategorias() == 0) {
            Menu.erro("Nenhuma categoria cadastrada.");
            Console.pausar();
            return;
        }

        for (int i = 0; i < estoque.getTotalCategorias(); i++) {
            Categoria c = estoque.getCategoria(i);
            System.out.println("\nCategoria: " + c.getNome());

            int totalItens = 0;
            double totalValor = 0;
            for (int j = 0; j < estoque.getTotalProdutos(); j++) {
                Produto p = estoque.getProduto(j);
                if (p.getCategoria() == c) {
                    System.out.println("- " + p.getNome()
                            + " | Qtde: " + p.getQuantidade()
                            + " | Valor: " + Menu.moeda(p.getValorTotal()));
                    totalItens += p.getQuantidade();
                    totalValor += p.getValorTotal();
                }
            }
            System.out.println("Total na categoria: " + totalItens
                    + " itens - " + Menu.moeda(totalValor));
        }

        Console.pausar();
    }

    /**
     * Cadastra algumas categorias e produtos para o sistema ja iniciar com dados.
     * Pode apagar este metodo (e a chamada no main) para comecar com o estoque vazio.
     */
    private static void carregarDadosExemplo() {
        Categoria eletronicos = new Categoria("Eletronicos");
        Categoria perifericos = new Categoria("Perifericos");
        estoque.adicionarCategoria(eletronicos);
        estoque.adicionarCategoria(perifericos);

        estoque.adicionarProduto(new Produto("Notebook", 3500.00, 15, eletronicos));
        estoque.adicionarProduto(new Produto("Smartphone", 2000.00, 20, eletronicos));
        estoque.adicionarProduto(new Produto("Mouse", 80.00, 5, perifericos));
        estoque.adicionarProduto(new Produto("Teclado", 120.00, 8, perifericos));
    }
}
