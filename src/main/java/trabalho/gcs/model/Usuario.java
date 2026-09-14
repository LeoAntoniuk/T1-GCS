package trabalho.gcs.model;

public class Usuario {
    private final int id;
    private final String nome;

    public Usuario(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getIniciais() {
        String[] partes = nome.trim().split("\\s+");

        if (partes.length == 1) {
            return partes[0].substring(0, 1).toUpperCase();
        }

        return (partes[0].substring(0, 1) + partes[partes.length - 1].substring(0, 1)).toUpperCase();
    }

    @Override
    public String toString() {
        return "[" + id + "] - " + nome + " (" + getIniciais() + ")";
    }
}
