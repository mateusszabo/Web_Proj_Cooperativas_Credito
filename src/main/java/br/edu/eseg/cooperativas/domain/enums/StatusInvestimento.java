package br.edu.eseg.cooperativas.domain.enums;

public enum StatusInvestimento {
    ATIVO("Ativo"),
    VENCIDO("Vencido"),
    RESGATADO("Resgatado");

    private final String descricao;

    StatusInvestimento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean podeTransitarPara(StatusInvestimento destino) {
        if (this == destino) return true;
        return switch (this) {
            case ATIVO -> destino == VENCIDO || destino == RESGATADO;
            case VENCIDO -> destino == RESGATADO;
            case RESGATADO -> false; // Estado terminal (bloqueia duplo resgate)
        };
    }
}