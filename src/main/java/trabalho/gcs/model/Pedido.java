package trabalho.gcs.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pedido {
    public static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final int id;
    private final Usuario solicitante;
    private final Departamento departamento;
    private final LocalDate dataPedido;
    private LocalDate dataConclusao;
    private StatusPedido status;
    private final List<ItemPedido> itens;

    public Pedido(int id, Usuario solicitante, LocalDate dataPedido, List<ItemPedido> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("O pedido deve ter pelo menos um item.");
        }
        this.id = id;
        this.solicitante = solicitante;
        this.departamento = solicitante.getDepartamento();
        this.dataPedido = dataPedido;
        this.status = StatusPedido.ABERTO;
        this.itens = new ArrayList<>(itens);
    }

    public int getId() {
        return id;
    }

    public Usuario getSolicitante() {
        return solicitante;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public LocalDate getDataPedido() {
        return dataPedido;
    }

    public LocalDate getDataConclusao() {
        return dataConclusao;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public double getValorTotal() {
        double total = 0;
        for (ItemPedido item : itens) {
            total += item.getTotal();
        }
        return total;
    }

    public boolean isAberto() {
        return status == StatusPedido.ABERTO;
    }

    public boolean contemItem(String trecho) {
        String busca = trecho.toLowerCase();
        for (ItemPedido item : itens) {
            if (item.getDescricao().toLowerCase().contains(busca)) {
                return true;
            }
        }
        return false;
    }

    public void aprovar() {
        verificarAberto();
        status = StatusPedido.APROVADO;
    }

    public void reprovar() {
        verificarAberto();
        status = StatusPedido.REPROVADO;
    }

    public void concluir(LocalDate data) {
        if (status != StatusPedido.APROVADO) {
            throw new IllegalStateException("Somente pedidos aprovados podem ser concluídos.");
        }
        status = StatusPedido.CONCLUIDO;
        dataConclusao = data;
    }

    private void verificarAberto() {
        if (!isAberto()) {
            throw new IllegalStateException("O pedido #" + id + " não está aberto (status: "
                    + status.getDescricao() + ") e não pode ser reaberto nem reavaliado.");
        }
    }

    public String resumo() {
        return String.format("#%-3d %s  %-25s %-12s %-10s R$ %,12.2f",
                id, dataPedido.format(FORMATO_DATA), solicitante.getNome(),
                departamento.getNome(), status.getDescricao(), getValorTotal());
    }

    public String detalhes() {
        StringBuilder sb = new StringBuilder();
        sb.append("Pedido #").append(id).append('\n');
        sb.append("  Solicitante : ").append(solicitante.getNome()).append(" (").append(solicitante.getIniciais()).append(")\n");
        sb.append("  Departamento: ").append(departamento.getNome()).append(String.format(" (limite R$ %,.2f)%n", departamento.getLimitePorPedido()));
        sb.append("  Data pedido : ").append(dataPedido.format(FORMATO_DATA)).append('\n');
        sb.append("  Conclusão   : ").append(dataConclusao == null ? "-" : dataConclusao.format(FORMATO_DATA)).append('\n');
        sb.append("  Status      : ").append(status.getDescricao()).append('\n');
        sb.append("  Itens:\n");
        for (ItemPedido item : itens) {
            sb.append("    - ").append(item).append('\n');
        }
        sb.append(String.format("  Valor total : R$ %,.2f", getValorTotal()));
        return sb.toString();
    }
}
