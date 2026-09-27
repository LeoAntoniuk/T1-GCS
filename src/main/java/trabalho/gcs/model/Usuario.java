package trabalho.gcs.model;

public abstract class Usuario {
    private final int id;
    private final String nome;
    private final Departamento departamento;

    public Usuario(int id, String nome, Departamento departamento) {
        this.id = id;
        this.nome = nome;
        this.departamento = departamento;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public String getIniciais() {
        String[] partes = nome.trim().split("\\s+");

        if (partes.length == 1) {
            return partes[0].substring(0, 1).toUpperCase();
        }

        return (partes[0].substring(0, 1) + partes[partes.length - 1].substring(0, 1)).toUpperCase();
    }

    public abstract TipoUsuario getTipo();

    @Override
    public String toString() {
        return "[" + id + "] - " + nome + " (" + getIniciais() + ")";
    }
}
