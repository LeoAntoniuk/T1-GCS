package trabalho.gcs.model;

public class Departamento {
    private final int id;
    private final String nome;
    private final double limitePorPedido;

    public Departamento(int id, String nome, double limitePorPedido) {
        this.id = id;
        this.nome = nome;
        this.limitePorPedido = limitePorPedido;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getLimitePorPedido() {
        return limitePorPedido;
    }

    @Override
    public String toString() {
        return String.format("[%d] %-12s limite por pedido: R$ %,.2f", id, nome, limitePorPedido);
    }
}
