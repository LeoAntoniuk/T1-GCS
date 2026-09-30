package trabalho.gcs.model;

public class Usuario {
    private final int id;
    private final String nome;
    private final TipoUsuario tipo;
    private final Departamento departamento;

    public Usuario(int id, String nome, TipoUsuario tipo, Departamento departamento) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.departamento = departamento;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public boolean isAdministrador() {
        return tipo == TipoUsuario.ADMINISTRADOR;
    }

    public String getIniciais() {
        StringBuilder sb = new StringBuilder();
        for (String parte : nome.trim().split("\\s+")) {
            if (parte.length() > 2 || parte.equals(parte.toUpperCase())) {
                sb.append(Character.toUpperCase(parte.charAt(0)));
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format("[%2d] %-25s (%s) - %-13s - %s", id, nome, getIniciais(), tipo.getDescricao(), departamento.getNome());
    }
}
