package br.edu.eseg.cooperativas.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "cooperativa",
        indexes = {
                @Index(name = "idx_coop_uf", columnList = "estado")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_coop_nome", columnNames = "nome")
        }
)
public class Cooperativa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cooperativa")
    private Long idCooperativa;

    @NotBlank(message = "O nome da cooperativa é obrigatório")
    @Column(name = "nome", nullable = false, unique = true, length = 120)
    private String nome;

    @NotBlank(message = "A cidade é obrigatória")
    @Column(name = "cidade", nullable = false, length = 80)
    private String cidade;

    @NotBlank(message = "A UF é obrigatória")
    @Size(min = 2, max = 2, message = "O estado deve conter a sigla UF de 2 caracteres")
    @Column(name = "estado", nullable = false, length = 2)
    private String estado;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @NotNull(message = "O ano de fundação é obrigatório")
    @Min(value = 1900, message = "Ano de fundação inválido")
    @Column(name = "ano_fundacao", nullable = false)
    private Integer anoFundacao;

    @NotNull
    @PositiveOrZero
    @Column(name = "agencias", nullable = false)
    private Integer agencias;

    @NotNull
    @PositiveOrZero
    @Column(name = "funcionarios", nullable = false)
    private Integer funcionarios;

    @NotNull
    @Positive
    @Digits(integer = 16, fraction = 2)
    @Column(name = "patrimonio_referencia", nullable = false, precision = 18, scale = 2)
    private BigDecimal patrimonioReferencia;

    @NotNull
    @DecimalMin("0.00")
    @Digits(integer = 3, fraction = 2)
    @Column(name = "indice_capitalizacao_percentual", nullable = false, precision = 5, scale = 2)
    private BigDecimal indiceCapitalizacaoPercentual;

    @OneToMany(mappedBy = "cooperativa", fetch = FetchType.LAZY)
    private List<Associado> associados = new ArrayList<>();

    @OneToMany(mappedBy = "cooperativa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<IndicadorMensal> indicadores = new ArrayList<>();

    public Cooperativa() {}

    public int getIdadeCooperativaAnos() {
        return Math.max(0, Year.now().getValue() - (this.anoFundacao != null ? this.anoFundacao : Year.now().getValue()));
    }

    public Long getIdCooperativa() { return idCooperativa; }
    public void setIdCooperativa(Long idCooperativa) { this.idCooperativa = idCooperativa; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public Integer getAnoFundacao() { return anoFundacao; }
    public void setAnoFundacao(Integer anoFundacao) { this.anoFundacao = anoFundacao; }
    public Integer getAgencias() { return agencias; }
    public void setAgencias(Integer agencias) { this.agencias = agencias; }
    public Integer getFuncionarios() { return funcionarios; }
    public void setFuncionarios(Integer funcionarios) { this.funcionarios = funcionarios; }
    public BigDecimal getPatrimonioReferencia() { return patrimonioReferencia; }
    public void setPatrimonioReferencia(BigDecimal patrimonioReferencia) { this.patrimonioReferencia = patrimonioReferencia; }
    public BigDecimal getIndiceCapitalizacaoPercentual() { return indiceCapitalizacaoPercentual; }
    public void setIndiceCapitalizacaoPercentual(BigDecimal indiceCapitalizacaoPercentual) { this.indiceCapitalizacaoPercentual = indiceCapitalizacaoPercentual; }
    public List<Associado> getAssociados() { return associados; }
    public void setAssociados(List<Associado> associados) { this.associados = associados; }
    public List<IndicadorMensal> getIndicadores() { return indicadores; }
    public void setIndicadores(List<IndicadorMensal> indicadores) { this.indicadores = indicadores; }
}