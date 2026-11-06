package br.edu.eseg.cooperativas.domain.model;

import br.edu.eseg.cooperativas.domain.enums.*;
import br.edu.eseg.cooperativas.exception.RegraNegocioException;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "operacao_credito",
        indexes = {
                @Index(name = "idx_op_assoc", columnList = "id_associado"),
                @Index(name = "idx_op_status", columnList = "status_contrato")
        }
)
public class OperacaoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_operacao")
    private Long idOperacao;

    @NotNull(message = "O associado tomador é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_associado", nullable = false, foreignKey = @ForeignKey(name = "fk_op_credito_associado"))
    private Associado associado;

    @NotNull(message = "O produto de crédito é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "produto_credito", nullable = false, length = 35)
    private ProdutoCredito produtoCredito;

    @NotNull(message = "A data de contratação é obrigatória")
    @Column(name = "data_contratacao", nullable = false)
    private LocalDate dataContratacao;

    @NotNull
    @Positive(message = "O valor contratado deve ser maior que zero")
    @Digits(integer = 13, fraction = 2)
    @Column(name = "valor_contratado", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorContratado;

    @NotNull
    @Positive(message = "O prazo em meses deve ser maior que zero")
    @Column(name = "prazo_meses", nullable = false)
    private Integer prazoMeses;

    @NotNull
    @Positive(message = "A taxa de juros mensal deve ser positiva")
    @Digits(integer = 4, fraction = 2)
    @Column(name = "taxa_juros_mensal_percentual", nullable = false, precision = 6, scale = 2)
    private BigDecimal taxaJurosMensalPercentual;

    @NotNull
    @Positive
    @Digits(integer = 13, fraction = 2)
    @Column(name = "parcela_mensal", nullable = false, precision = 15, scale = 2)
    private BigDecimal parcelaMensal;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "risco_credito", nullable = false, length = 10)
    private RiscoCredito riscoCredito;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "garantia", nullable = false, length = 30)
    private TipoGarantia garantia;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_contrato", nullable = false, length = 20)
    private StatusContrato statusContrato = StatusContrato.ATIVO;

    @NotNull
    @PositiveOrZero
    @Column(name = "dias_atraso", nullable = false)
    private Integer diasAtraso = 0;

    @OneToMany(mappedBy = "operacaoCredito", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<RegistroInadimplencia> historicoInadimplencia = new ArrayList<>();

    public OperacaoCredito() {}

    /**
     * RF05: Motor de calculo automatico da Parcela Mensal pelo Sistema Frances de Amortizacao (Tabela Price).
     * Formula: PMT = PV * [ i * (1 + i)^n ] / [ (1 + i)^n - 1 ]
     */
    public BigDecimal calcularParcelaPrice() {
        if (valorContratado == null || prazoMeses == null || prazoMeses <= 0 || taxaJurosMensalPercentual == null) {
            throw new RegraNegocioException("Parâmetros insuficientes para cálculo da Tabela Price.");
        }
        MathContext mc = MathContext.DECIMAL64;
        BigDecimal taxaDecimal = taxaJurosMensalPercentual.divide(BigDecimal.valueOf(100), mc);

        if (taxaDecimal.compareTo(BigDecimal.ZERO) == 0) {
            this.parcelaMensal = valorContratado.divide(BigDecimal.valueOf(prazoMeses), 2, RoundingMode.HALF_UP);
            return this.parcelaMensal;
        }

        BigDecimal umMaisI = BigDecimal.ONE.add(taxaDecimal, mc);
        BigDecimal fatorPotencia = umMaisI.pow(prazoMeses, mc);
        BigDecimal numerador = taxaDecimal.multiply(fatorPotencia, mc);
        BigDecimal denominador = fatorPotencia.subtract(BigDecimal.ONE, mc);

        this.parcelaMensal = valorContratado
                .multiply(numerador.divide(denominador, mc), mc)
                .setScale(2, RoundingMode.HALF_UP);
        return this.parcelaMensal;
    }

    /**
     * RF04 e Requisito 2.3: Maquina de estados deterministica para contratos de credito.
     */
    public void alterarStatus(StatusContrato novoStatus) {
        if (!this.statusContrato.podeTransitarPara(novoStatus)) {
            throw new RegraNegocioException(
                    String.format("Transição de status inválida na Operação de Crédito: de %s para %s.",
                            this.statusContrato, novoStatus));
        }
        if (novoStatus == StatusContrato.QUITADO
                || novoStatus == StatusContrato.CANCELADO
                || novoStatus == StatusContrato.RENEGOCIADO) {
            this.diasAtraso = 0;
        }
        if (novoStatus == StatusContrato.ATIVO && this.diasAtraso != null && this.diasAtraso > 0) {
            this.statusContrato = StatusContrato.EM_ATRASO;
            return;
        }
        this.statusContrato = novoStatus;
    }

    public void atualizarDiasAtraso(int novosDiasAtraso) {
        if (novosDiasAtraso < 0) {
            throw new RegraNegocioException("A quantidade de dias de atraso não pode ser negativa.");
        }
        if (this.statusContrato.isTerminal() && novosDiasAtraso > 0) {
            throw new RegraNegocioException(
                    "Operação bloqueada: contratos QUITADOS ou CANCELADOS não podem receber apontamento de atraso.");
        }
        this.diasAtraso = novosDiasAtraso;
        if (novosDiasAtraso > 0 && (this.statusContrato == StatusContrato.ATIVO || this.statusContrato == StatusContrato.RENEGOCIADO)) {
            this.statusContrato = StatusContrato.EM_ATRASO;
        } else if (novosDiasAtraso == 0 && this.statusContrato == StatusContrato.EM_ATRASO) {
            this.statusContrato = StatusContrato.ATIVO;
        }
    }

    public Long getIdOperacao() { return idOperacao; }
    public void setIdOperacao(Long idOperacao) { this.idOperacao = idOperacao; }
    public Associado getAssociado() { return associado; }
    public void setAssociado(Associado associado) { this.associado = associado; }
    public ProdutoCredito getProdutoCredito() { return produtoCredito; }
    public void setProdutoCredito(ProdutoCredito produtoCredito) { this.produtoCredito = produtoCredito; }
    public LocalDate getDataContratacao() { return dataContratacao; }
    public void setDataContratacao(LocalDate dataContratacao) { this.dataContratacao = dataContratacao; }
    public BigDecimal getValorContratado() { return valorContratado; }
    public void setValorContratado(BigDecimal valorContratado) { this.valorContratado = valorContratado; }
    public Integer getPrazoMeses() { return prazoMeses; }
    public void setPrazoMeses(Integer prazoMeses) { this.prazoMeses = prazoMeses; }
    public BigDecimal getTaxaJurosMensalPercentual() { return taxaJurosMensalPercentual; }
    public void setTaxaJurosMensalPercentual(BigDecimal taxaJurosMensalPercentual) { this.taxaJurosMensalPercentual = taxaJurosMensalPercentual; }
    public BigDecimal getParcelaMensal() { return parcelaMensal; }
    public void setParcelaMensal(BigDecimal parcelaMensal) { this.parcelaMensal = parcelaMensal; }
    public RiscoCredito getRiscoCredito() { return riscoCredito; }
    public void setRiscoCredito(RiscoCredito riscoCredito) { this.riscoCredito = riscoCredito; }
    public TipoGarantia getGarantia() { return garantia; }
    public void setGarantia(TipoGarantia garantia) { this.garantia = garantia; }
    public StatusContrato getStatusContrato() { return statusContrato; }
    public void setStatusContrato(StatusContrato statusContrato) { this.statusContrato = statusContrato; }
    public Integer getDiasAtraso() { return diasAtraso; }
    public void setDiasAtraso(Integer diasAtraso) { this.diasAtraso = diasAtraso; }
    public List<RegistroInadimplencia> getHistoricoInadimplencia() { return historicoInadimplencia; }
    public void setHistoricoInadimplencia(List<RegistroInadimplencia> historicoInadimplencia) { this.historicoInadimplencia = historicoInadimplencia; }
}