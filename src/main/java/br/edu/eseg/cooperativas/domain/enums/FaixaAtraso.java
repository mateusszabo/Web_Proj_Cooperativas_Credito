package br.edu.eseg.cooperativas.domain.enums;

public enum FaixaAtraso {
    SEM_ATRASO("Sem atraso"),
    DE_1_A_30_DIAS("1-30 dias"),
    DE_31_A_60_DIAS("31-60 dias"),
    DE_61_A_90_DIAS("61-90 dias"),
    ACIMA_90_DIAS("Acima de 90 dias");

    private final String descricao;

    FaixaAtraso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static FaixaAtraso fromDiasAtraso(int dias) {
        if (dias <= 0) return SEM_ATRASO;
        if (dias <= 30) return DE_1_A_30_DIAS;
        if (dias <= 60) return DE_31_A_60_DIAS;
        if (dias <= 90) return DE_61_A_90_DIAS;
        return ACIMA_90_DIAS;
    }
}