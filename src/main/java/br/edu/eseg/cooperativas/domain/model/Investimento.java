package br.edu.eseg.cooperativas.domain.model;

import br.edu.eseg.cooperativas.domain.enums.*;
import br.edu.eseg.cooperativas.exception.RegraNegocioException;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "investimento",
        indexes = {
                @Index(name = "idx_inv_assoc", columnList = "id_associado"),
                @Index(name = "idx_inv_status", columnList = "status_investimento")
        }
)
public class Investimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_investimento")
    private Long idInvestimento;

    @NotNull(message = "O associado investidor é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_associado", nullable = false, foreignKey = @ForeignKey(name = "fk_investimento_associado"))
    private Associado associado;

    @NotNull(message = "A data de aplicação é obrigatória")
    @Column(name = "data_aplicacao", nullable = false)
    private LocalDate dataAplicacao;

    @NotNull(message = "O produto de investimento é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "produto_investimento", nullable = false, length = 35)
    private ProdutoInvestimento produtoInvestimento;

    @NotNull
    @Positive
    @Digits(integer = 13, fraction = 2)
    @Column(name = "valor_aplicado", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorAplicado;

    @NotNull
    @Positive
    @Digits(integer = 4, fraction = 2)
    @Column(name = "rentabilidade_anual_percentual", nullable = false, precision = 6, scale = 2)
    private BigDecimal rentabilidadeAnualPercentual;

    @NotNull
    @Positive
    @Column(name = "prazo_dias", nullable = false)
    private Integer prazoDias;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "liquidez", nullable = false, length = 20)
    private LiquidezInvestimento liquidez;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_investimento", nullable = false, length = 20)
    private StatusInvestimento statusInvestimento = StatusInvestimento.ATIVO;

    public Investimento() {}

    public LocalDate getDataVencimento() {
        return dataAplicacao != null && prazoDias != null ? dataAplicacao.plusDays(prazoDias) : null;
    }

    public void resgatar(LocalDate dataReferencia) {
        if (!this.statusInvestimento.podeTransitarPara(StatusInvestimento.RESGATADO)) {
            throw new RegraNegocioException("Este investimento já foi resgatado (RESGATADO é estado terminal).");
        }
        LocalDate vencimento = getDataVencimento();
        if (this.liquidez == LiquidezInvestimento.VENCIMENTO && vencimento != null && dataReferencia.isBefore(vencimento)) {
            throw new RegraNegocioException(
                    "Resgate bloqueado: aplicação com liquidez 'No Vencimento' só pode ser resgatada a partir de " + vencimento);
        }
        if (this.liquidez.getCarenciaMinimaDias() > 0 && dataAplicacao != null) {
            LocalDate fimCarencia = dataAplicacao.plusDays(this.liquidez.getCarenciaMinimaDias());
            if (dataReferencia.isBefore(fimCarencia)) {
                throw new RegraNegocioException(
                        "Resgate bloqueado: carência mínima de " + this.liquidez.getDescricao() + " encerra em " + fimCarencia);
            }
        }
        this.statusInvestimento = StatusInvestimento.RESGATADO;
    }

    public Long getIdInvestimento() { return idInvestimento; }
    public void setIdInvestimento(Long idInvestimento) { this.idInvestimento = idInvestimento; }
    public Associado getAssociado() { return associado; }
    public void setAssociado(Associado associado) { this.associado = associado; }
    public LocalDate getDataAplicacao() { return dataAplicacao; }
    public void setDataAplicacao(LocalDate dataAplicacao) { this.dataAplicacao = dataAplicacao; }
    public ProdutoInvestimento getProdutoInvestimento() { return produtoInvestimento; }
    public void setProdutoInvestimento(ProdutoInvestimento produtoInvestimento) { this.produtoInvestimento = produtoInvestimento; }
    public BigDecimal getValorAplicado() { return valorAplicado; }
    public void setValorAplicado(BigDecimal valorAplicado) { this.valorAplicado = valorAplicado; }
    public BigDecimal getRentabilidadeAnualPercentual() { return rentabilidadeAnualPercentual; }
    public void setRentabilidadeAnualPercentual(BigDecimal rentabilidadeAnualPercentual) { this.rentabilidadeAnualPercentual = rentabilidadeAnualPercentual; }
    public Integer getPrazoDias() { return prazoDias; }
    public void setPrazoDias(Integer prazoDias) { this.prazoDias = prazoDias; }
    public LiquidezInvestimento getLiquidez() { return liquidez; }
    public void setLiquidez(LiquidezInvestimento liquidez) { this.liquidez = liquidez; }
    public StatusInvestimento getStatusInvestimento() { return statusInvestimento; }
    public void setStatusInvestimento(StatusInvestimento statusInvestimento) { this.statusInvestimento = statusInvestimento; }
}