package br.edu.eseg.cooperativas.domain.enums;

public enum StatusContrato {
    ATIVO("Ativo"),
    EM_ATRASO("Em atraso"),
    RENEGOCIADO("Renegociado"),
    QUITADO("Quitado"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusContrato(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isTerminal() {
        return this == QUITADO || this == CANCELADO;
    }

    public boolean podeTransitarPara(StatusContrato destino) {
        if (this == destino) return true;
        return switch (this) {
            case ATIVO -> destino == EM_ATRASO || destino == QUITADO || destino == CANCELADO;
            case EM_ATRASO -> destino == ATIVO || destino == RENEGOCIADO;
            case RENEGOCIADO -> destino == EM_ATRASO || destino == QUITADO;
            case QUITADO, CANCELADO -> false; // Estados terminais imutaveis
        };
    }
}