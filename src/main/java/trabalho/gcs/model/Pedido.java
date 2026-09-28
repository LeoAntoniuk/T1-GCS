package trabalho.gcs.model;

import java.util.ArrayList;
import java.util.List;
public class Pedido {

    private final int id;
    private final Usuario solicitador;
    private final Departamento departamento;
    private final String dataPedido;
    private String dataConclusao;
    private StatusPedido status;
    private final List<ItemPedido> itens = new ArrayList<>();

    public Pedido(int id, Usuario solicitador, String dataPedido) {
        this.id = id;
        this.solicitador = solicitador;
        this.dataPedido = dataPedido;
        this.departamento = solicitador.getDepartamento();
        this.status = StatusPedido.ABERTO;
    }

    public int getId() {
        return id;
    }

    public Usuario getSolicitador() {
        return solicitador;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public String getDataPedido() {
        return dataPedido;
    }

    public String getDataConclusao() {
        return dataConclusao;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public double getValorTotal() {
        double total = 0;
        for (ItemPedido item : itens) {
            total += item.getValorTotal();
        }
        return total;   
    }

    public void setDataConclusao(String dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

}
