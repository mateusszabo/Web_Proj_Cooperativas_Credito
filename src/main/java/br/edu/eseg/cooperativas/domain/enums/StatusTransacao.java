package br.edu.eseg.cooperativas.domain.enums;

public enum StatusTransacao {
    PENDENTE("Pendente"),
    CONCLUIDA("Concluída"),
    RECUSADA("Recusada"),
    ESTORNADA("Estornada");

    private final String descricao;

    StatusTransacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean podeTransitarPara(StatusTransacao destino) {
        if (this == destino) return true;
        return switch (this) {
            case PENDENTE -> destino == CONCLUIDA || destino == RECUSADA;
            case CONCLUIDA -> destino == ESTORNADA;
            case RECUSADA, ESTORNADA -> false; // Estados terminais
        };
    }
}