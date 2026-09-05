package gerenciacontato.gui;

import gerenciacontato.model.Contato;
import gerenciacontato.service.ContatoService;
import java.util.List;
import java.util.Scanner;

public class ConsoleGUI {

    private ContatoService service;
    private Scanner sc;

    public ConsoleGUI() {
        this.service = new ContatoService();
        this.sc = new Scanner(System.in);
    }

    public void exibirMenu() {
        int opcao = -1;

        while (opcao != 0) {
            System.out.println("=== AGENDA DE CONTATOS ===");
            System.out.println("1. Listar Contatos");
            System.out.println("2. Cadastrar Contato");
            System.out.println("3. Atualizar Contato");
            System.out.println("4. Excluir Contato");
            System.out.println("0. Sair \n");
            System.out.print("Digite uma opção: ");

            try {
                opcao = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException ex) {
                System.out.print("Opção inválida! Digite apenas numeros.");
                continue;
            }

            switch (opcao) {
                case 1:
                    listar();
                    break;
                case 2:
                    cadastrar();
                    break;
                case 3:
                    atualizar();
                    break;
                case 4:
                    excluir();
                    break;
                case 0:
                    System.out.println("Saindo do sistema... ");
                    break;
            }
        }
    }

    private void atualizar() {
        System.out.print("\nDigite o ID do contato a atualizar: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            Contato existente = service.buscarPorId(id);

            if (existente == null) {
                System.out.println("Nenhum contato encontrado com esse ID " + id);
                return;
            }

            System.out.println("Contato atual: " + existente);
            System.out.print("Novo nome (ou Enter para manter '" + existente.getNome() + "'): ");
            String novoNome = sc.nextLine().trim();
            if (novoNome.isEmpty()) {
                novoNome = existente.getNome();
            }

            System.out.print("Novo telefone (ou Enter para manter '" + existente.getTelefone() + "'): ");
            String novoTelefone = sc.nextLine().trim();
            if (novoTelefone.isEmpty()) {
                novoTelefone = existente.getTelefone();
            }

            System.out.print("Novo email (ou Enter para manter '" + existente.getEmail() + "'): ");
            String novoEmail = sc.nextLine().trim();
            if (novoEmail.isEmpty()) {
                novoEmail = existente.getEmail();
            }
            
            boolean sucesso = service.atualizarContato(id, novoNome, novoTelefone, novoEmail);
            
            if (sucesso) {
                System.out.println("Contato atualizado com sucesso!");
            } else {
                System.out.println("Falha ao atualizar o contato.");
            }
        } catch (NumberFormatException ex) {
            System.out.println("O ID precisa ser um numero inteiro");
        }
    }

    private void excluir() {
        System.out.print("\nDigite o ID do contato a excluir: ");
        try {
            int id = Integer.parseInt(sc.nextLine());
            Contato contato = service.buscarPorId(id);

            if (contato == null) {
                System.out.println("Nenhum contato encontrado com o ID " + id);
                return;
            }

            System.out.println("Contato selecionado: " + contato);
            System.out.print("Deseja realmente excluir este contato? (S/N): ");
            String confirmacao = sc.nextLine().trim();

            if (confirmacao.equalsIgnoreCase("S")) {
                boolean sucesso = service.excluirContato(id);
                if (sucesso) {
                    System.out.println("Contato excluído com sucesso!");
                } else {
                    System.out.println("Falha ao excluir contato.");
                }
            } else {
                System.out.println("Operação cancelada.");
            }
        } catch (NumberFormatException ex) {
            System.out.println("O ID precisa ser um número inteiro.");
        }
    }

    private void cadastrar() {
        System.out.print("Nome: ");
        String nome = sc.nextLine();
        System.out.print("Telefone: ");
        String telefone = sc.nextLine();
        System.out.print("E-mail: ");
        String email = sc.nextLine();

        service.cadastrarContato(nome, telefone, email);
    }

    private void listar() {
        List<Contato> contatos = service.listarContatos();
        System.out.println("\n--- Lista de Contatos ---");

        if (contatos.isEmpty()) {
            System.out.println("Nenhum contato cadastrado.");
        } else {
            for (Contato c : contatos) {
                System.out.println(c);
            }
        }
    }
}
