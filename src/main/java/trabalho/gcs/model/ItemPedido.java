package trabalho.gcs.model;

public class ItemPedido {
    private final String descricao;
    private final double valorUnitario;
    private final int quantidade;

    public ItemPedido(String descricao, double valorUnitario, int quantidade) {
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição do item não pode ser vazia.");
        }
        if (valorUnitario <= 0) {
            throw new IllegalArgumentException("O valor unitário deve ser maior que zero.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        this.descricao = descricao.trim();
        this.valorUnitario = valorUnitario;
        this.quantidade = quantidade;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getTotal() {
        return valorUnitario * quantidade;
    }

    @Override
    public String toString() {
        return String.format("%-35s %4d x R$ %,10.2f = R$ %,12.2f",
                descricao, quantidade, valorUnitario, getTotal());
    }
}
