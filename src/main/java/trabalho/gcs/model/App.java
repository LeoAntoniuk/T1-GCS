package trabalho.gcs.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class App {

    public void run() {
        Sistema sistema = new Sistema();
        inicializarDados(sistema);

        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Sistema de Controlo de Aquisições ===");

        do {
            sistema.setUsuarioAtual(null);
            System.out.println("\n--- Seleção de Utilizador ---");
            System.out.println("0. Sair do Sistema");
            for (Usuario u : sistema.getUsuarios()) {
                System.out.println(u);
            }

            System.out.print("\nIntroduza o ID do utilizador (ou 0 para sair): ");
            int id = lerInteiro(scanner);

            if (id == 0) {
                System.out.println("A encerrar o sistema...");
                break;
            }

            Usuario usuario = sistema.buscarUsuario(id);
            if (usuario == null) {
                System.out.println("Utilizador não encontrado!");
                continue;
            }

            sistema.setUsuarioAtual(usuario);
            executarMenu(sistema, scanner);

        } while (true);

        scanner.close();
    }

    private static void executarMenu(Sistema sistema, Scanner scanner) {
        Usuario atual = sistema.getUsuarioAtual();
        int opcao;

        do {
            System.out.println("\n=== Menu Principal ===");
            System.out.println("Utilizador Atual: " + atual.getNome() + " (" + atual.getTipo().getDescricao() + ")");
            System.out.println("1. Registar novo pedido");
            System.out.println("2. Ver os meus pedidos");
            System.out.println("3. Excluir um pedido meu (apenas abertos)");
            System.out.println("4. Ver detalhes de um pedido");

            if (atual.isAdministrador()) {
                System.out.println("5. Avaliar pedido (Aprovar/Rejeitar)");
                System.out.println("6. Concluir pedido aprovado");
                System.out.println("7. Listar todos os pedidos entre datas");
                System.out.println("8. Buscar pedidos por funcionário solicitante");
                System.out.println("9. Buscar pedidos por descrição de item");
                System.out.println("10. Ver estatísticas gerais");
            }

            System.out.println("0. Trocar de Utilizador");
            System.out.print("Escolha uma opção: ");
            opcao = lerInteiro(scanner);

            try {
                switch (opcao) {
                    case 1: registarPedidoMenu(sistema, scanner); break;
                    case 2: listarPedidos(sistema.pedidosDoUsuarioAtual()); break;
                    case 3: excluirPedidoMenu(sistema, scanner); break;
                    case 4: verDetalhesMenu(sistema, scanner); break;
                    case 5: seAdmin(atual); avaliarPedidoMenu(sistema, scanner); break;
                    case 6: seAdmin(atual); concluirPedidoMenu(sistema, scanner); break;
                    case 7: seAdmin(atual); listarEntreDatasMenu(sistema, scanner); break;
                    case 8: seAdmin(atual); buscarPorSolicitanteMenu(sistema, scanner); break;
                    case 9: seAdmin(atual); buscarPorItemMenu(sistema, scanner); break;
                    case 10: seAdmin(atual); System.out.println("\n" + sistema.estatisticas()); break;
                    case 0: System.out.println("A terminar sessão do utilizador..."); break;
                    default: System.out.println("Opção inválida!");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }

        } while (opcao != 0);
    }

    private static void seAdmin(Usuario u) {
        if (!u.isAdministrador()) throw new IllegalStateException("Opção exclusiva para administradores.");
    }

    private static void registarPedidoMenu(Sistema sistema, Scanner scanner) {
        System.out.println("\n--- Registar Novo Pedido ---");
        List<ItemPedido> itens = new ArrayList<>();
        boolean adicionarMais = true;

        while (adicionarMais) {
            System.out.print("Descrição do item: ");
            String desc = scanner.nextLine();
            if (desc.trim().isEmpty()) {
                System.out.println("Descrição não pode ser vazia.");
                continue;
            }

            System.out.print("Valor unitário: R$ ");
            double valor = lerDouble(scanner);
            System.out.print("Quantidade: ");
            int qtd = lerInteiro(scanner);

            try {
                itens.add(new ItemPedido(desc, valor, qtd));
                System.out.println("Item adicionado com sucesso.");
            } catch (IllegalArgumentException e) {
                System.out.println("Erro ao adicionar item: " + e.getMessage());
            }

            System.out.print("Deseja adicionar mais itens? (S/N): ");
            adicionarMais = scanner.nextLine().trim().equalsIgnoreCase("S");
        }

        if (!itens.isEmpty()) {
            Pedido p = sistema.registrarPedido(itens);
            System.out.println("Pedido registado com sucesso! ID: " + p.getId());
        } else {
            System.out.println("Registo cancelado: nenhum item inserido.");
        }
    }

    private static void excluirPedidoMenu(Sistema sistema, Scanner scanner) {
        System.out.print("\nIntroduza o ID do pedido que deseja excluir: ");
        int id = lerInteiro(scanner);
        Pedido p = sistema.buscarPedido(id);
        if (p == null) {
            System.out.println("Pedido não encontrado.");
            return;
        }
        sistema.excluirPedido(p);
        System.out.println("Pedido excluído com sucesso.");
    }

    private static void verDetalhesMenu(Sistema sistema, Scanner scanner) {
        System.out.print("\nIntroduza o ID do pedido: ");
        int id = lerInteiro(scanner);
        Pedido p = sistema.buscarPedido(id);
        if (p == null) {
            System.out.println("Pedido não encontrado.");
            return;
        }
        Usuario atual = sistema.getUsuarioAtual();
        if (!atual.isAdministrador() && p.getSolicitante() != atual) {
            throw new IllegalStateException("Só pode consultar os detalhes dos seus próprios pedidos.");
        }
        System.out.println("\n" + p.detalhes());
    }

    private static void avaliarPedidoMenu(Sistema sistema, Scanner scanner) {
        System.out.print("\nIntroduza o ID do pedido a avaliar: ");
        int id = lerInteiro(scanner);
        Pedido p = sistema.buscarPedido(id);

        if (p == null) {
            System.out.println("Pedido não encontrado.");
            return;
        }

        System.out.println(p.detalhes());
        System.out.println("\n1. Aprovar");
        System.out.println("2. Rejeitar");
        System.out.println("0. Cancelar");
        System.out.print("Escolha a ação: ");
        int acao = lerInteiro(scanner);

        if (acao == 1) {
            sistema.avaliarPedido(p, true);
            System.out.println("Pedido aprovado.");
        } else if (acao == 2) {
            sistema.avaliarPedido(p, false);
            System.out.println("Pedido rejeitado.");
        }
    }

    private static void concluirPedidoMenu(Sistema sistema, Scanner scanner) {
        System.out.print("\nIntroduza o ID do pedido a concluir: ");
        int id = lerInteiro(scanner);
        Pedido p = sistema.buscarPedido(id);
        if (p != null) {
            sistema.concluirPedido(p);
            System.out.println("Pedido marcado como concluído.");
        } else {
            System.out.println("Pedido não encontrado.");
        }
    }

    private static void listarEntreDatasMenu(Sistema sistema, Scanner scanner) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try {
            System.out.print("Data inicial (DD/MM/AAAA): ");
            LocalDate inicio = LocalDate.parse(scanner.nextLine(), fmt);
            System.out.print("Data final (DD/MM/AAAA): ");
            LocalDate fim = LocalDate.parse(scanner.nextLine(), fmt);
            listarPedidos(sistema.listarEntreDatas(inicio, fim));
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data inválido.");
        }
    }

    private static void buscarPorSolicitanteMenu(Sistema sistema, Scanner scanner) {
        System.out.print("Introduza o ID do funcionário: ");
        int id = lerInteiro(scanner);
        Usuario u = sistema.buscarUsuario(id);
        if (u != null) {
            listarPedidos(sistema.buscarPorSolicitante(u));
        } else {
            System.out.println("Utilizador não encontrado.");
        }
    }

    private static void buscarPorItemMenu(Sistema sistema, Scanner scanner) {
        System.out.print("Introduza a descrição (ou parte dela) do item: ");
        String trecho = scanner.nextLine();
        listarPedidos(sistema.buscarPorDescricaoItem(trecho));
    }

    private static void listarPedidos(List<Pedido> pedidos) {
        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido encontrado.");
            return;
        }
        System.out.println("\n--- Lista de Pedidos ---");
        for (Pedido p : pedidos) {
            System.out.println(p.resumo());
        }
    }

    private static int lerInteiro(Scanner scanner) {
        while (true) {
            try {
                int valor = Integer.parseInt(scanner.nextLine().trim());
                return valor;
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Introduza um número inteiro: ");
            }
        }
    }

    private static double lerDouble(Scanner scanner) {
        while (true) {
            try {
                double valor = Double.parseDouble(scanner.nextLine().trim().replace(',', '.'));
                return valor;
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Introduza um número válido: ");
            }
        }
    }

    private static Pedido novoPedido(Sistema sistema, int idSolicitante, int diasAtras, ItemPedido... itens) {
        return sistema.registrarPedido(
                sistema.buscarUsuario(idSolicitante),
                LocalDate.now().minusDays(diasAtras),
                List.of(itens));
    }

    private static void inicializarDados(Sistema sistema) {
        Departamento d1 = new Departamento(1, "Financeiro", 50000);
        Departamento d2 = new Departamento(2, "RH", 15000);
        Departamento d3 = new Departamento(3, "Engenharia", 100000);
        Departamento d4 = new Departamento(4, "Manutenção", 30000);
        Departamento d5 = new Departamento(5, "Tecnologia", 80000);

        sistema.adicionarDepartamento(d1);
        sistema.adicionarDepartamento(d2);
        sistema.adicionarDepartamento(d3);
        sistema.adicionarDepartamento(d4);
        sistema.adicionarDepartamento(d5);

        sistema.adicionarUsuario(new Usuario(1, "Ana Silva", TipoUsuario.ADMINISTRADOR, d1));
        sistema.adicionarUsuario(new Usuario(2, "Bruno Costa", TipoUsuario.FUNCIONARIO, d1));
        sistema.adicionarUsuario(new Usuario(3, "Carlos Pereira", TipoUsuario.FUNCIONARIO, d1));

        sistema.adicionarUsuario(new Usuario(4, "Diana Ramos", TipoUsuario.ADMINISTRADOR, d2));
        sistema.adicionarUsuario(new Usuario(5, "Eduardo Lima", TipoUsuario.FUNCIONARIO, d2));
        sistema.adicionarUsuario(new Usuario(6, "Fernanda Alves", TipoUsuario.FUNCIONARIO, d2));

        sistema.adicionarUsuario(new Usuario(7, "Gustavo Santos", TipoUsuario.FUNCIONARIO, d3));
        sistema.adicionarUsuario(new Usuario(8, "Helena Moura", TipoUsuario.FUNCIONARIO, d3));
        sistema.adicionarUsuario(new Usuario(9, "Igor Nogueira", TipoUsuario.ADMINISTRADOR, d3));

        sistema.adicionarUsuario(new Usuario(10, "Joana Dias", TipoUsuario.FUNCIONARIO, d4));
        sistema.adicionarUsuario(new Usuario(11, "Kleber Machado", TipoUsuario.FUNCIONARIO, d4));
        sistema.adicionarUsuario(new Usuario(12, "Luísa Castro", TipoUsuario.ADMINISTRADOR, d4));

        sistema.adicionarUsuario(new Usuario(13, "Marcos Vinícius", TipoUsuario.ADMINISTRADOR, d5));
        sistema.adicionarUsuario(new Usuario(14, "Natália Gomes", TipoUsuario.FUNCIONARIO, d5));
        sistema.adicionarUsuario(new Usuario(15, "Otávio Mendes", TipoUsuario.FUNCIONARIO, d5));

        sistema.setUsuarioAtual(sistema.buscarUsuario(2));
        Pedido p1 = novoPedido(sistema, 2, 75,
                new ItemPedido("Cadeiras de Escritório", 450.00, 5),
                new ItemPedido("Mesa de Reunião", 1200.00, 1));
        Pedido p2 = novoPedido(sistema, 14, 40,
                new ItemPedido("Monitor Dell 27", 1500.00, 4));
        Pedido p3 = novoPedido(sistema, 7, 28,
                new ItemPedido("Multímetro Digital", 350.00, 2),
                new ItemPedido("Estação de Solda", 850.00, 1));
        Pedido p4 = novoPedido(sistema, 5, 22,
                new ItemPedido("Licenças de Software de Folha", 3200.00, 2),
                new ItemPedido("Formação Online", 900.00, 3));
        Pedido p5 = novoPedido(sistema, 10, 18,
                new ItemPedido("Compressor de Ar", 7800.00, 1),
                new ItemPedido("Kit de Ferramentas", 640.00, 3));
        Pedido p6 = novoPedido(sistema, 3, 12,
                new ItemPedido("Notebook", 4800.00, 4),
                new ItemPedido("Rato sem fios", 90.00, 4));
        Pedido p7 = novoPedido(sistema, 8, 9,
                new ItemPedido("Impressora 3D", 12500.00, 1),
                new ItemPedido("Filamento PLA", 120.00, 10));
        novoPedido(sistema, 15, 6,
                new ItemPedido("Servidor de Rede", 32000.00, 1),
                new ItemPedido("Switch 24 portas", 2800.00, 2));
        novoPedido(sistema, 11, 3,
                new ItemPedido("Capacete de Segurança", 45.00, 20),
                new ItemPedido("Luvas Industriais", 28.00, 40));
        novoPedido(sistema, 6, 2,
                new ItemPedido("Cadeira Ergonómica", 780.00, 6));
        novoPedido(sistema, 2, 1,
                new ItemPedido("Papel A4 (caixa)", 260.00, 10));
        novoPedido(sistema, 14, 0,
                new ItemPedido("Monitor Dell 27", 1500.00, 2),
                new ItemPedido("Webcam HD", 220.00, 2));
        novoPedido(sistema, 7, 0,
                new ItemPedido("Osciloscópio Digital", 6200.00, 1));
 
        // Avaliações feitas por administradores
        sistema.setUsuarioAtual(sistema.buscarUsuario(1));
        sistema.avaliarPedido(p1, true);
        sistema.concluirPedido(p1);
        sistema.avaliarPedido(p6, false);
 
        sistema.setUsuarioAtual(sistema.buscarUsuario(13));
        sistema.avaliarPedido(p2, false);
 
        sistema.setUsuarioAtual(sistema.buscarUsuario(9));
        sistema.avaliarPedido(p3, true);
        sistema.avaliarPedido(p7, true);
 
        sistema.setUsuarioAtual(sistema.buscarUsuario(4));
        sistema.avaliarPedido(p4, true);
        sistema.concluirPedido(p4);
 
        sistema.setUsuarioAtual(sistema.buscarUsuario(12));
        sistema.avaliarPedido(p5, true);
 
        sistema.setUsuarioAtual(null);
    }
}