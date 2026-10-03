package trabalho.gcs.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Sistema {
    private final List<Departamento> departamentos = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Pedido> pedidos = new ArrayList<>();
    private Usuario usuarioAtual;
    private int proximoIdPedido = 1;

    public void adicionarDepartamento(Departamento d) {
        departamentos.add(d);
    }

    public void adicionarUsuario(Usuario u) {
        usuarios.add(u);
    }

    public List<Departamento> getDepartamentos() {
        return Collections.unmodifiableList(departamentos);
    }

    public List<Usuario> getUsuarios() {
        return Collections.unmodifiableList(usuarios);
    }

    public List<Pedido> getPedidos() {
        return Collections.unmodifiableList(pedidos);
    }

    public Usuario buscarUsuario(int id) {
        for (Usuario u : usuarios) {
            if (u.getId() == id) {
                return u;
            }
        }
        return null;
    }

    public Pedido buscarPedido(int id) {
        for (Pedido p : pedidos) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public Usuario getUsuarioAtual() {
        return usuarioAtual;
    }

    public void setUsuarioAtual(Usuario usuario) {
        this.usuarioAtual = usuario;
    }

    public Pedido registrarPedido(List<ItemPedido> itens) {
        return registrarPedido(usuarioAtual, LocalDate.now(), itens);
    }

    public Pedido registrarPedido(Usuario solicitante, LocalDate data, List<ItemPedido> itens) {
        Pedido pedido = new Pedido(proximoIdPedido, solicitante, data, itens);
        double limite = pedido.getDepartamento().getLimitePorPedido();
        if (pedido.getValorTotal() > limite) {
            throw new IllegalArgumentException(String.format(
                    "Valor total R$ %,.2f excede o limite de R$ %,.2f do departamento %s.",
                    pedido.getValorTotal(), limite, pedido.getDepartamento().getNome()));
        }
        proximoIdPedido++;
        pedidos.add(pedido);
        return pedido;
    }

    public void avaliarPedido(Pedido pedido, boolean aprovar) {
        exigirAdministrador();
        if (aprovar) {
            pedido.aprovar();
        } else {
            pedido.reprovar();
        }
    }

    public void concluirPedido(Pedido pedido) {
        exigirAdministrador();
        pedido.concluir(LocalDate.now());
    }

    public void excluirPedido(Pedido pedido) {
        if (pedido.getSolicitante() != usuarioAtual) {
            throw new IllegalStateException("Somente o funcionário que criou o pedido pode excluí-lo.");
        }
        if (!pedido.isAberto()) {
            throw new IllegalStateException("Somente pedidos abertos podem ser excluídos.");
        }
        pedidos.remove(pedido);
    }

    public List<Pedido> pedidosDoUsuarioAtual() {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (p.getSolicitante() == usuarioAtual) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Pedido> listarEntreDatas(LocalDate inicio, LocalDate fim) {
        exigirAdministrador();
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : pedidos) {
            LocalDate d = p.getDataPedido();
            if (!d.isBefore(inicio) && !d.isAfter(fim)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Pedido> buscarPorSolicitante(Usuario solicitante) {
        exigirAdministrador();
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (p.getSolicitante() == solicitante) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public List<Pedido> buscarPorDescricaoItem(String trecho) {
        exigirAdministrador();
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (p.contemItem(trecho)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    public String estatisticas() {
        exigirAdministrador();

        int total      = pedidos.size();
        int aprovados  = contarPorStatus(StatusPedido.APROVADO);
        int concluidos = contarPorStatus(StatusPedido.CONCLUIDO);
        int reprovados = contarPorStatus(StatusPedido.REPROVADO);
        int abertos    = contarPorStatus(StatusPedido.ABERTO);
        double valorTotal = somarValores(pedidos);

        List<Pedido> ultimos30 = pedidosDesde(LocalDate.now().minusDays(30));
        double valorUltimos30  = somarValores(ultimos30);
        double mediaUltimos30  = ultimos30.isEmpty() ? 0 : valorUltimos30 / ultimos30.size();

        Pedido maiorAberto = maiorPedidoAberto();

        StringBuilder sb = new StringBuilder();

        sb.append("=== Estatísticas Gerais ===\n");
        sb.append(String.format("Total de pedidos: %d  (valor acumulado: R$ %,.2f)%n", total, valorTotal));
        sb.append(linhaStatus("Aprovados",  aprovados + concluidos, total));
        sb.append(linhaStatus("  - Concluídos", concluidos, total));
        sb.append(linhaStatus("Reprovados", reprovados, total));
        sb.append(linhaStatus("Abertos",    abertos, total));
        sb.append('\n');

        sb.append("Últimos 30 dias:\n");
        sb.append(String.format("  Pedidos    : %d%n", ultimos30.size()));
        sb.append(String.format("  Valor total: R$ %,.2f%n", valorUltimos30));
        sb.append(String.format("  Valor médio: R$ %,.2f%n", mediaUltimos30));
        sb.append('\n');

        sb.append("Pedido aberto de maior valor:\n");
        sb.append(maiorAberto == null ? "  (nenhum pedido aberto)" : maiorAberto.detalhes());

        return sb.toString();
    }

    private int contarPorStatus(StatusPedido status) {
        int count = 0;
        for (Pedido p : pedidos) {
            if (p.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    private static double somarValores(List<Pedido> lista) {
        double soma = 0;
        for (Pedido p : lista) {
            soma += p.getValorTotal();
        }
        return soma;
    }

    private List<Pedido> pedidosDesde(LocalDate data) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : pedidos) {
            if (!p.getDataPedido().isBefore(data)) {
                resultado.add(p);
            }
        }
        return resultado;
    }

    private Pedido maiorPedidoAberto() {
        Pedido maior = null;
        for (Pedido p : pedidos) {
            if (p.isAberto() && (maior == null || p.getValorTotal() > maior.getValorTotal())) {
                maior = p;
            }
        }
        return maior;
    }

    private static String linhaStatus(String rotulo, int quantidade, int total) {
        return String.format("  %-14s %3d  (%5.1f%%)%n", rotulo + ":", quantidade, percentual(quantidade, total));
    }

    private static double percentual(int parte, int total) {
        return total == 0 ? 0 : parte * 100.0 / total;
    }

    private void exigirAdministrador() {
        if (usuarioAtual == null || !usuarioAtual.isAdministrador()) {
            throw new IllegalStateException("Operação permitida somente para administradores.");
        }
    }
}
