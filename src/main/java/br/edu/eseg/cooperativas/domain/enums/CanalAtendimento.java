package br.edu.eseg.cooperativas.domain.enums;

public enum CanalAtendimento {
    APLICATIVO("Aplicativo"),
    AGENCIA("Agência"),
    INTERNET_BANKING("Internet Banking"),
    TELEFONE("Telefone"),
    CORRESPONDENTE("Correspondente");

    private final String descricao;
    CanalAtendimento(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}