package trabalho.gcs.model;

public enum StatusPedido {
    ABERTO("Aberto"),
    APROVADO("Aprovado"),
    REPROVADO("Reprovado"),
    CONCLUIDO("Concluído");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
