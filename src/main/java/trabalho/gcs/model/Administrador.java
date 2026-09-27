package trabalho.gcs.model;

public class Administrador extends Usuario {
    private String email;
    private int senha;

    public Administrador(int id,String nome, String email, int senha, Departamento departamento) {
        super(id, nome, departamento);
        this.email = email;
        this.senha = senha;
    }

    @Override

    public TipoUsuario getTipo() {
        return TipoUsuario.ADMINISTRADOR;
    }
}
