package br.edu.eseg.cooperativas.domain.enums;

public enum FaixaIdade {
    FAIXA_18_25("18-25", 18),
    FAIXA_26_35("26-35", 26),
    FAIXA_36_45("36-45", 36),
    FAIXA_46_60("46-60", 46),
    FAIXA_61_MAIS("61+", 61);

    private final String rotulo;
    private final int idadeMinima;
    FaixaIdade(String rotulo, int idadeMinima) {
        this.rotulo = rotulo;
        this.idadeMinima = idadeMinima;
    }
    public String getRotulo() { return rotulo; }
    public int getIdadeMinima() { return idadeMinima; }
}