package br.edu.eseg.cooperativas.domain.model;

import br.edu.eseg.cooperativas.domain.enums.*;
import br.edu.eseg.cooperativas.exception.RegraNegocioException;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "transacao",
        indexes = {
                @Index(name = "idx_trans_conta", columnList = "id_conta"),
                @Index(name = "idx_trans_data", columnList = "data_hora"),
                @Index(name = "idx_trans_status", columnList = "status_transacao")
        }
)
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transacao")
    private Long idTransacao;

    @NotNull(message = "A conta bancária de origem é obrigatória")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_conta", nullable = false, foreignKey = @ForeignKey(name = "fk_transacao_conta"))
    private Conta conta;

    @NotNull(message = "A data e hora da transação são obrigatórias")
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;

    @NotNull(message = "O tipo de transação é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_transacao", nullable = false, length = 25)
    private TipoTransacao tipoTransacao;

    @NotNull(message = "O canal de atendimento é obrigatório")
    @Enumerated(EnumType.STRING)
    @Column(name = "canal", nullable = false, length = 25)
    private CanalAtendimento canal;

    @NotNull
    @Positive(message = "O valor da transação deve ser maior que zero")
    @Digits(integer = 13, fraction = 2)
    @Column(name = "valor_transacao", nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTransacao;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 8, fraction = 2)
    @Column(name = "tarifa_transacao", nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifaTransacao = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status_transacao", nullable = false, length = 20)
    private StatusTransacao statusTransacao = StatusTransacao.PENDENTE;

    @NotBlank
    @Column(name = "cidade", nullable = false, length = 80)
    private String cidade;

    @NotBlank
    @Size(min = 2, max = 2)
    @Column(name = "estado", nullable = false, length = 2)
    private String estado;

    public Transacao() {}

    public void alterarStatus(StatusTransacao novoStatus) {
        if (!this.statusTransacao.podeTransitarPara(novoStatus)) {
            throw new RegraNegocioException(
                    String.format("Transição inválida para Transação: de %s para %s (apenas CONCLUIDA permite estorno).",
                            this.statusTransacao, novoStatus));
        }
        this.statusTransacao = novoStatus;
    }

    public Long getIdTransacao() { return idTransacao; }
    public void setIdTransacao(Long idTransacao) { this.idTransacao = idTransacao; }
    public Conta getConta() { return conta; }
    public void setConta(Conta conta) { this.conta = conta; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
    public TipoTransacao getTipoTransacao() { return tipoTransacao; }
    public void setTipoTransacao(TipoTransacao tipoTransacao) { this.tipoTransacao = tipoTransacao; }
    public CanalAtendimento getCanal() { return canal; }
    public void setCanal(CanalAtendimento canal) { this.canal = canal; }
    public BigDecimal getValorTransacao() { return valorTransacao; }
    public void setValorTransacao(BigDecimal valorTransacao) { this.valorTransacao = valorTransacao; }
    public BigDecimal getTarifaTransacao() { return tarifaTransacao; }
    public void setTarifaTransacao(BigDecimal tarifaTransacao) { this.tarifaTransacao = tarifaTransacao; }
    public StatusTransacao getStatusTransacao() { return statusTransacao; }
    public void setStatusTransacao(StatusTransacao statusTransacao) { this.statusTransacao = statusTransacao; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}