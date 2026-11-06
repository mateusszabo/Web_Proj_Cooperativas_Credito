package br.edu.eseg.cooperativas.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "indicador_mensal",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_ind_coop_mes", columnNames = {"id_cooperativa", "mes_referencia"})
        }
)
public class IndicadorMensal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_indicador")
    private Long idIndicador;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cooperativa", nullable = false, foreignKey = @ForeignKey(name = "fk_indicador_cooperativa"))
    private Cooperativa cooperativa;

    @NotBlank
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$")
    @Column(name = "mes_referencia", nullable = false, length = 7)
    private String mesReferencia;

    @NotNull
    @PositiveOrZero
    @Column(name = "associados_ativos", nullable = false)
    private Integer associadosAtivos;

    @NotNull
    @PositiveOrZero
    @Column(name = "carteira_credito_total", nullable = false, precision = 18, scale = 2)
    private BigDecimal carteiraCreditoTotal;

    @NotNull
    @PositiveOrZero
    @Column(name = "depositos_totais", nullable = false, precision = 18, scale = 2)
    private BigDecimal depositosTotais;

    @NotNull
    @Column(name = "resultado_liquido", nullable = false, precision = 18, scale = 2)
    private BigDecimal resultadoLiquido;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "indice_inadimplencia_percentual", nullable = false, precision = 5, scale = 2)
    private BigDecimal indiceInadimplenciaPercentual;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "indice_eficiencia_percentual", nullable = false, precision = 5, scale = 2)
    private BigDecimal indiceEficienciaPercentual;

    @NotNull
    @DecimalMin("0.00")
    @Column(name = "operacoes_digitais_percentual", nullable = false, precision = 5, scale = 2)
    private BigDecimal operacoesDigitaisPercentual;

    @NotNull
    @PositiveOrZero
    @Column(name = "receita_servicos", nullable = false, precision = 18, scale = 2)
    private BigDecimal receitaServicos;

    @NotNull
    @DecimalMin("0.00")
    @DecimalMax("10.00")
    @Column(name = "satisfacao_associado", nullable = false, precision = 4, scale = 2)
    private BigDecimal satisfacaoAssociado;

    public IndicadorMensal() {}

    public Long getIdIndicador() { return idIndicador; }
    public void setIdIndicador(Long idIndicador) { this.idIndicador = idIndicador; }
    public Cooperativa getCooperativa() { return cooperativa; }
    public void setCooperativa(Cooperativa cooperativa) { this.cooperativa = cooperativa; }
    public String getMesReferencia() { return mesReferencia; }
    public void setMesReferencia(String mesReferencia) { this.mesReferencia = mesReferencia; }
    public Integer getAssociadosAtivos() { return associadosAtivos; }
    public void setAssociadosAtivos(Integer associadosAtivos) { this.associadosAtivos = associadosAtivos; }
    public BigDecimal getCarteiraCreditoTotal() { return carteiraCreditoTotal; }
    public void setCarteiraCreditoTotal(BigDecimal carteiraCreditoTotal) { this.carteiraCreditoTotal = carteiraCreditoTotal; }
    public BigDecimal getDepositosTotais() { return depositosTotais; }
    public void setDepositosTotais(BigDecimal depositosTotais) { this.depositosTotais = depositosTotais; }
    public BigDecimal getResultadoLiquido() { return resultadoLiquido; }
    public void setResultadoLiquido(BigDecimal resultadoLiquido) { this.resultadoLiquido = resultadoLiquido; }
    public BigDecimal getIndiceInadimplenciaPercentual() { return indiceInadimplenciaPercentual; }
    public void setIndiceInadimplenciaPercentual(BigDecimal indiceInadimplenciaPercentual) { this.indiceInadimplenciaPercentual = indiceInadimplenciaPercentual; }
    public BigDecimal getIndiceEficienciaPercentual() { return indiceEficienciaPercentual; }
    public void setIndiceEficienciaPercentual(BigDecimal indiceEficienciaPercentual) { this.indiceEficienciaPercentual = indiceEficienciaPercentual; }
    public BigDecimal getOperacoesDigitaisPercentual() { return operacoesDigitaisPercentual; }
    public void setOperacoesDigitaisPercentual(BigDecimal operacoesDigitaisPercentual) { this.operacoesDigitaisPercentual = operacoesDigitaisPercentual; }
    public BigDecimal getReceitaServicos() { return receitaServicos; }
    public void setReceitaServicos(BigDecimal receitaServicos) { this.receitaServicos = receitaServicos; }
    public BigDecimal getSatisfacaoAssociado() { return satisfacaoAssociado; }
    public void setSatisfacaoAssociado(BigDecimal satisfacaoAssociado) { this.satisfacaoAssociado = satisfacaoAssociado; }
}