package br.edu.eseg.cooperativas.domain.enums;

public enum ProdutoCredito {
    FINANCIAMENTO_RURAL("Financiamento Rural"),
    ANTECIPACAO_RECEBIVEIS("Antecipação de Recebíveis"),
    CAPITAL_DE_GIRO("Capital de Giro"),
    CREDITO_IMOBILIARIO("Crédito Imobiliário"),
    FINANCIAMENTO_VEICULO("Financiamento de Veículo"),
    CREDITO_PESSOAL("Crédito Pessoal"),
    CREDITO_CONSIGNADO("Crédito Consignado");

    private final String descricao;
    ProdutoCredito(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}