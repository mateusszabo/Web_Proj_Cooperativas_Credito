package br.edu.eseg.cooperativas.domain.model;

import br.edu.eseg.cooperativas.domain.enums.*;
import br.edu.eseg.cooperativas.exception.RegraNegocioException;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "conta",
        indexes = {
                @Index(name = "idx_conta_assoc", columnList = "id_associado"),
                @Index(name = "idx_conta_tipo", columnList = "tipo_conta")
        }
)
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conta")
    private Long idConta;

    @NotNull(message = "O associado titular é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_associado", nullable = false, foreignKey = @ForeignKey(name = "fk_conta_associado"))
    private Associado associado;

    @NotNull(message = "O tipo de conta é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conta", nullable = false, length = 25)
    private TipoConta tipoConta;

    @NotNull(message = "A data de abertura é obrigatória")
    @Column(name = "data_abertura", nullable = false)
    private LocalDate dataAbertura;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 13, fraction = 2)
    @Column(name = "saldo_medio_mensal", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldoMedioMensal = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 13, fraction = 2)
    @Column(name = "limite_credito", nullable = false, precision = 15, scale = 2)
    private BigDecimal limiteCredito = BigDecimal.ZERO;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 8, fraction = 2)
    @Column(name = "tarifa_mensal", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaMensal = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_conta", nullable = false, length = 20)
    private StatusConta statusConta = StatusConta.ATIVA;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "canal_abertura", nullable = false, length = 25)
    private CanalAtendimento canalAbertura;

    @OneToMany(mappedBy = "conta", fetch = FetchType.LAZY)
    private List<Transacao> transacoes = new ArrayList<>();

    public Conta() {}

    public void alterarStatus(StatusConta novoStatus) {
        if (!this.statusConta.podeTransitarPara(novoStatus)) {
            throw new RegraNegocioException(
                    String.format("Transição inválida para Conta: de %s para %s (ENCERRADA é estado terminal).",
                            this.statusConta, novoStatus));
        }
        if (novoStatus == StatusConta.ATIVA && this.associado != null
                && this.associado.getStatusAssociado() != StatusAssociado.ATIVO) {
            throw new RegraNegocioException("Proibido ativar ou reabrir conta de Associado que não esteja ATIVO.");
        }
        this.statusConta = novoStatus;
    }

    public Long getIdConta() { return idConta; }
    public void setIdConta(Long idConta) { this.idConta = idConta; }
    public Associado getAssociado() { return associado; }
    public void setAssociado(Associado associado) { this.associado = associado; }
    public TipoConta getTipoConta() { return tipoConta; }
    public void setTipoConta(TipoConta tipoConta) { this.tipoConta = tipoConta; }
    public LocalDate getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDate dataAbertura) { this.dataAbertura = dataAbertura; }
    public BigDecimal getSaldoMedioMensal() { return saldoMedioMensal; }
    public void setSaldoMedioMensal(BigDecimal saldoMedioMensal) { this.saldoMedioMensal = saldoMedioMensal; }
    public BigDecimal getLimiteCredito() { return limiteCredito; }
    public void setLimiteCredito(BigDecimal limiteCredito) { this.limiteCredito = limiteCredito; }
    public BigDecimal getTarifaMensal() { return tarifaMensal; }
    public void setTarifaMensal(BigDecimal tarifaMensal) { this.tarifaMensal = tarifaMensal; }
    public StatusConta getStatusConta() { return statusConta; }
    public void setStatusConta(StatusConta statusConta) { this.statusConta = statusConta; }
    public CanalAtendimento getCanalAbertura() { return canalAbertura; }
    public void setCanalAbertura(CanalAtendimento canalAbertura) { this.canalAbertura = canalAbertura; }
    public List<Transacao> getTransacoes() { return transacoes; }
    public void setTransacoes(List<Transacao> transacoes) { this.transacoes = transacoes; }
}