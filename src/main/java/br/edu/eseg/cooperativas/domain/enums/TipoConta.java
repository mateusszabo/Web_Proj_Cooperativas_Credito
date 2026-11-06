package br.edu.eseg.cooperativas.domain.enums;

public enum TipoConta {
    CONTA_CORRENTE("Conta Corrente"),
    CONTA_DIGITAL("Conta Digital"),
    CONTA_POUPANCA("Conta Poupança"),
    CONTA_SALARIO("Conta Salário");

    private final String descricao;
    TipoConta(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}