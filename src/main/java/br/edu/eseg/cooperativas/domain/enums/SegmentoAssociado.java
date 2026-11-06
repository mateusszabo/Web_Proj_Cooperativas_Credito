package br.edu.eseg.cooperativas.domain.enums;

public enum SegmentoAssociado {
    PESSOA_FISICA("Pessoa Física"),
    PRODUTOR_RURAL("Produtor Rural"),
    PROFISSIONAL_LIBERAL("Profissional Liberal"),
    MICROEMPRESA("Microempresa"),
    PEQUENA_EMPRESA("Pequena Empresa"),
    COOPERADO_INSTITUCIONAL("Cooperado Institucional");

    private final String descricao;
    SegmentoAssociado(String descricao) { this.descricao = descricao; }
    public String getDescricao() { return descricao; }
}