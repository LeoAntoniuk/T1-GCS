package trabalho.gcs.model;

public class Departamento {
    private final int id;
    private final String nome;
    private final double limitePedido;
    
    public Departamento(int id, String nome, double limitePedido) {
        this.id = id;
        this.nome = nome;
        this.limitePedido = limitePedido;
    }
    
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public double getLimitePedido() {
        return limitePedido;
    }

    @Override
    public String toString() {
        return "[" + id + "] - " + nome + " (Limite: R$" + limitePedido + ")";
    }
}
