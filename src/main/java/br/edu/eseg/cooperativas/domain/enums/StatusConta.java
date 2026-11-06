package br.edu.eseg.cooperativas.domain.enums;

public enum StatusConta {
    ATIVA("Ativa"),
    BLOQUEADA("Bloqueada"),
    ENCERRADA("Encerrada");

    private final String descricao;

    StatusConta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean podeTransitarPara(StatusConta destino) {
        if (this == destino) return true;
        return switch (this) {
            case ATIVA -> destino == BLOQUEADA || destino == ENCERRADA;
            case BLOQUEADA -> destino == ATIVA || destino == ENCERRADA;
            case ENCERRADA -> false; // Estado terminal imutavel
        };
    }
}