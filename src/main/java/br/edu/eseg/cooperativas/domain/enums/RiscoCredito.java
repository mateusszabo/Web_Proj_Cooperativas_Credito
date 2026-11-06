package br.edu.eseg.cooperativas.domain.enums;

public enum RiscoCredito {
    AA("AA - Risco Mínimo"),
    A("A - Risco Muito Baixo"),
    B("B - Risco Baixo"),
    C("C - Risco Moderado"),
    D("D - Risco Alto"),
    E("E - Risco Crítico");

    private final String descricao;

    RiscoCredito(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static RiscoCredito classificarPorScore(int score) {
        if (score >= 850) return AA;
        if (score >= 750) return A;
        if (score >= 600) return B;
        if (score >= 500) return C;
        if (score >= 400) return D;
        return E;
    }
}