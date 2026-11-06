package br.edu.eseg.cooperativas.domain.model;

import br.edu.eseg.cooperativas.domain.enums.*;
import br.edu.eseg.cooperativas.exception.RegraNegocioException;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "associado",
        indexes = {
                @Index(name = "idx_assoc_coop", columnList = "id_cooperativa"),
                @Index(name = "idx_assoc_status", columnList = "status_associado")
        }
)
public class Associado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_associado")
    private Long idAssociado;

    @NotNull(message = "A cooperativa de vínculo é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cooperativa", nullable = false, foreignKey = @ForeignKey(name = "fk_associado_cooperativa"))
    private Cooperativa cooperativa;

    @NotNull(message = "O segmento do associado é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "segmento", nullable = false, length = 35)
    private SegmentoAssociado segmento;

    @NotBlank(message = "A cidade do associado é obrigatória")
    @Column(name = "cidade", nullable = false, length = 80)
    private String cidade;

    @NotBlank(message = "O estado (UF) é obrigatório")
    @Size(min = 2, max = 2)
    @Column(name = "estado", nullable = false, length = 2)
    private String estado;

    @NotNull(message = "A faixa de idade é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(name = "idade_faixa", nullable = false, length = 20)
    private FaixaIdade idadeFaixa;

    @NotNull
    @PositiveOrZero(message = "O tempo de associação não pode ser negativo")
    @Column(name = "tempo_associado_anos", nullable = false)
    private Integer tempoAssociadoAnos;

    @NotNull
    @Positive(message = "A renda mensal estimada deve ser positiva")
    @Digits(integer = 13, fraction = 2)
    @Column(name = "renda_mensal_estimada", nullable = false, precision = 15, scale = 2)
    private BigDecimal rendaMensalEstimada;

    @NotNull
    @Min(value = 0, message = "O score mínimo é 0")
    @Max(value = 1000, message = "O score máximo é 1000")
    @Column(name = "score_credito", nullable = false)
    private Integer scoreCredito;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_associado", nullable = false, length = 20)
    private StatusAssociado statusAssociado = StatusAssociado.ATIVO;

    @OneToMany(mappedBy = "associado", fetch = FetchType.LAZY)
    private List<Conta> contas = new ArrayList<>();

    @OneToMany(mappedBy = "associado", fetch = FetchType.LAZY)
    private List<OperacaoCredito> operacoesCredito = new ArrayList<>();

    @OneToMany(mappedBy = "associado", fetch = FetchType.LAZY)
    private List<Investimento> investimentos = new ArrayList<>();

    public Associado() {}

    // Substitui a coluna legada inconsistente possui_conta_digital (Secao 2.3 e 5.3)
    public boolean isPossuiContaDigitalAtiva() {
        return contas != null && contas.stream()
                .anyMatch(c -> c.getTipoConta() == TipoConta.CONTA_DIGITAL && c.getStatusConta() == StatusConta.ATIVA);
    }

    public void alterarStatus(StatusAssociado novoStatus) {
        if (!this.statusAssociado.podeTransitarPara(novoStatus)) {
            throw new RegraNegocioException(
                    String.format("Transição inválida para Associado: de %s para %s.", this.statusAssociado, novoStatus));
        }
        if (novoStatus == StatusAssociado.INATIVO) {
            boolean temContaAtiva = contas.stream().anyMatch(c -> c.getStatusConta() == StatusConta.ATIVA);
            boolean temCreditoPendente = operacoesCredito.stream()
                    .anyMatch(op -> !op.getStatusContrato().isTerminal());
            boolean temInvestimentoAtivo = investimentos.stream()
                    .anyMatch(inv -> inv.getStatusInvestimento() == StatusInvestimento.ATIVO);

            if (temContaAtiva || temCreditoPendente || temInvestimentoAtivo) {
                throw new RegraNegocioException(
                        "Bloqueado inativar associado que possua Conta ATIVA, Operação de Crédito em aberto ou Investimento ATIVO.");
            }
        }
        this.statusAssociado = novoStatus;
    }

    public Long getIdAssociado() { return idAssociado; }
    public void setIdAssociado(Long idAssociado) { this.idAssociado = idAssociado; }
    public Cooperativa getCooperativa() { return cooperativa; }
    public void setCooperativa(Cooperativa cooperativa) { this.cooperativa = cooperativa; }
    public SegmentoAssociado getSegmento() { return segmento; }
    public void setSegmento(SegmentoAssociado segmento) { this.segmento = segmento; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public FaixaIdade getIdadeFaixa() { return idadeFaixa; }
    public void setIdadeFaixa(FaixaIdade idadeFaixa) { this.idadeFaixa = idadeFaixa; }
    public Integer getTempoAssociadoAnos() { return tempoAssociadoAnos; }
    public void setTempoAssociadoAnos(Integer tempoAssociadoAnos) { this.tempoAssociadoAnos = tempoAssociadoAnos; }
    public BigDecimal getRendaMensalEstimada() { return rendaMensalEstimada; }
    public void setRendaMensalEstimada(BigDecimal rendaMensalEstimada) { this.rendaMensalEstimada = rendaMensalEstimada; }
    public Integer getScoreCredito() { return scoreCredito; }
    public void setScoreCredito(Integer scoreCredito) { this.scoreCredito = scoreCredito; }
    public StatusAssociado getStatusAssociado() { return statusAssociado; }
    public void setStatusAssociado(StatusAssociado statusAssociado) { this.statusAssociado = statusAssociado; }
    public List<Conta> getContas() { return contas; }
    public void setContas(List<Conta> contas) { this.contas = contas; }
    public List<OperacaoCredito> getOperacoesCredito() { return operacoesCredito; }
    public void setOperacoesCredito(List<OperacaoCredito> operacoesCredito) { this.operacoesCredito = operacoesCredito; }
    public List<Investimento> getInvestimentos() { return investimentos; }
    public void setInvestimentos(List<Investimento> investimentos) { this.investimentos = investimentos; }
}