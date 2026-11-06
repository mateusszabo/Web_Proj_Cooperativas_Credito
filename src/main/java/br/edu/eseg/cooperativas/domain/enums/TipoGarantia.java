package br.edu.eseg.cooperativas.domain.enums;

public enum TipoGarantia {
    IMOVEL("Imóvel"),
    APLICACAO_FINANCEIRA("Aplicação financeira"),
    ESTOQUE("Estoque"),
    VEICULO("Veículo"),
    SEM_GARANTIA("Sem garantia"),
    RECEBIVEIS("Recebíveis"),
    AVAL("Aval");

    private final String descricao;
    TipoGarantia(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}