package br.edu.eseg.cooperativas.domain.enums;

public enum PerfilUsuario {
    ROLE_ANALISTA("Analista Operacional"),
    ROLE_GESTOR("Gestor de Cooperativa");

    private final String descricao;
    PerfilUsuario(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}