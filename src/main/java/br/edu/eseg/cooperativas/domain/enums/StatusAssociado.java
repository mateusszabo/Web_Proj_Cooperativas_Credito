package br.edu.eseg.cooperativas.domain.enums;

public enum StatusAssociado {
    ATIVO("Ativo"),
    SUSPENSO("Suspenso"),
    INATIVO("Inativo");

    private final String descricao;

    StatusAssociado(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean podeTransitarPara(StatusAssociado destino) {
        if (this == destino) return true;
        return switch (this) {
            case ATIVO -> destino == SUSPENSO || destino == INATIVO;
            case SUSPENSO -> destino == ATIVO || destino == INATIVO;
            case INATIVO -> false; // Reativacao bloqueada sem novo processo cadastral
        };
    }
}