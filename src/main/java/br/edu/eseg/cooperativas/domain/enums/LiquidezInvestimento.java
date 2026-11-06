package br.edu.eseg.cooperativas.domain.enums;

public enum LiquidezInvestimento {
    DIARIA("Diária", 0),
    DIAS_30("30 dias", 30),
    DIAS_90("90 dias", 90),
    DIAS_180("180 dias", 180),
    VENCIMENTO("Vencimento", -1);

    private final String descricao;
    private final int carenciaMinimaDias;
    LiquidezInvestimento(String descricao, int carenciaMinimaDias) {
        this.descricao = descricao;
        this.carenciaMinimaDias = carenciaMinimaDias;
    }
    public String getDescricao() { return descricao; }
    public int getCarenciaMinimaDias() { return carenciaMinimaDias; }
}