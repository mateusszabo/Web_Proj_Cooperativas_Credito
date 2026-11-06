package br.edu.eseg.cooperativas.domain.enums;

public enum ProdutoInvestimento {
    COTA_CAPITAL("Cota Capital"),
    FUNDO_DI("Fundo DI"),
    POUPANCA_COOPERATIVA("Poupança Cooperativa"),
    LCA("LCA"),
    PREVIDENCIA_COOPERATIVA("Previdência Cooperativa"),
    RDC("RDC");

    private final String descricao;
    ProdutoInvestimento(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}