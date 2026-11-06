package br.edu.eseg.cooperativas.domain.enums;

public enum TipoTransacao {
    PIX("Pix", true),
    TED("TED", true),
    BOLETO("Boleto", true),
    DEBITO("Débito", true),
    SAQUE("Saque", true),
    APLICACAO("Aplicação", true),
    CREDITO("Crédito", false),
    DEPOSITO("Depósito", false),
    RESGATE("Resgate", false);

    private final String descricao;
    private final boolean saidaFinanceira;
    TipoTransacao(String descricao, boolean saidaFinanceira) {
        this.descricao = descricao;
        this.saidaFinanceira = saidaFinanceira;
    }
    public String getDescricao() { return descricao; }
    public boolean isSaidaFinanceira() { return saidaFinanceira; }
}