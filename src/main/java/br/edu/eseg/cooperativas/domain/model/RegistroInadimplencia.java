package br.edu.eseg.cooperativas.domain.model;

import br.edu.eseg.cooperativas.domain.enums.FaixaAtraso;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

@Entity
@Table(
        name = "registro_inadimplencia",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_inad_op_mes", columnNames = {"id_operacao", "mes_referencia"})
        }
)
public class RegistroInadimplencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registro")
    private Long idRegistro;

    @NotNull(message = "A operação de crédito vinculada é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_operacao", nullable = false, foreignKey = @ForeignKey(name = "fk_inadimplencia_operacao"))
    private OperacaoCredito operacaoCredito;

    @NotBlank(message = "O mês de referência (AAAA-MM) é obrigatório")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "O mês de referência deve seguir o formato AAAA-MM")
    @Column(name = "mes_referencia", nullable = false, length = 7)
    private String mesReferencia;

    @NotNull
    @Positive
    @Digits(integer = 13, fraction = 2)
    @Column(name = "saldo_devedor", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoDevedor;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 13, fraction = 2)
    @Column(name = "valor_em_atraso", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorEmAtraso;

    @NotNull
    @PositiveOrZero
    @Column(name = "dias_atraso", nullable = false)
    private Integer diasAtraso;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "faixa_atraso", nullable = false, length = 25)
    private FaixaAtraso faixaAtraso;

    @NotNull
    @DecimalMin("0.00")
    @DecimalMax("100.00")
    @Digits(integer = 3, fraction = 2)
    @Column(name = "probabilidade_default_percentual", nullable = false, precision = 5, scale = 2)
    private BigDecimal probabilidadeDefaultPercentual;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 13, fraction = 2)
    @Column(name = "provisao_perda_credito", nullable = false, precision = 15, scale = 2)
    private BigDecimal provisaoPerdaCredito;

    public RegistroInadimplencia() {}

    /**
     * RF05: Deriva automaticamente a Faixa de Atraso e calcula a PCLD (Provisao para Credito de Liquidacao Duvidosa).
     */
    @PrePersist
    @PreUpdate
    public void calcularFaixaEProvisao() {
        if (this.diasAtraso != null) {
            this.faixaAtraso = FaixaAtraso.fromDiasAtraso(this.diasAtraso);
        }
        if (this.saldoDevedor != null && this.probabilidadeDefaultPercentual != null) {
            this.provisaoPerdaCredito = this.saldoDevedor
                    .multiply(this.probabilidadeDefaultPercentual, MathContext.DECIMAL64)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
    }

    public Long getIdRegistro() { return idRegistro; }
    public void setIdRegistro(Long idRegistro) { this.idRegistro = idRegistro; }
    public OperacaoCredito getOperacaoCredito() { return operacaoCredito; }
    public void setOperacaoCredito(OperacaoCredito operacaoCredito) { this.operacaoCredito = operacaoCredito; }
    public String getMesReferencia() { return mesReferencia; }
    public void setMesReferencia(String mesReferencia) { this.mesReferencia = mesReferencia; }
    public BigDecimal getSaldoDevedor() { return saldoDevedor; }
    public void setSaldoDevedor(BigDecimal saldoDevedor) { this.saldoDevedor = saldoDevedor; }
    public BigDecimal getValorEmAtraso() { return valorEmAtraso; }
    public void setValorEmAtraso(BigDecimal valorEmAtraso) { this.valorEmAtraso = valorEmAtraso; }
    public Integer getDiasAtraso() { return diasAtraso; }
    public void setDiasAtraso(Integer diasAtraso) { this.diasAtraso = diasAtraso; }
    public FaixaAtraso getFaixaAtraso() { return faixaAtraso; }
    public void setFaixaAtraso(FaixaAtraso faixaAtraso) { this.faixaAtraso = faixaAtraso; }
    public BigDecimal getProbabilidadeDefaultPercentual() { return probabilidadeDefaultPercentual; }
    public void setProbabilidadeDefaultPercentual(BigDecimal probabilidadeDefaultPercentual) { this.probabilidadeDefaultPercentual = probabilidadeDefaultPercentual; }
    public BigDecimal getProvisaoPerdaCredito() { return provisaoPerdaCredito; }
    public void setProvisaoPerdaCredito(BigDecimal provisaoPerdaCredito) { this.provisaoPerdaCredito = provisaoPerdaCredito; }
}