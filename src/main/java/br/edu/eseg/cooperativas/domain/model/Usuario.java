package br.edu.eseg.cooperativas.domain.model;

import br.edu.eseg.cooperativas.domain.enums.PerfilUsuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
        name = "usuario",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_usuario_username", columnNames = "username")
        }
)
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @NotBlank
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank
    @Column(name = "senha_hash", nullable = false, length = 120)
    private String senhaHash;

    @NotBlank
    @Column(name = "nome_completo", nullable = false, length = 100)
    private String nomeCompleto;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false, length = 25)
    private PerfilUsuario perfil;

    @NotNull
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    public Usuario() {}

    public Usuario(String username, String senhaHash, String nomeCompleto, PerfilUsuario perfil, Boolean ativo) {
        this.username = username;
        this.senhaHash = senhaHash;
        this.nomeCompleto = nomeCompleto;
        this.perfil = perfil;
        this.ativo = ativo;
    }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getSenhaHash() { return senhaHash; }
    public void setSenhaHash(String senhaHash) { this.senhaHash = senhaHash; }
    public String getNomeCompleto() { return nomeCompleto; }
    public void setNomeCompleto(String nomeCompleto) { this.nomeCompleto = nomeCompleto; }
    public PerfilUsuario getPerfil() { return perfil; }
    public void setPerfil(PerfilUsuario perfil) { this.perfil = perfil; }
    public Boolean getAtivo() { return ativo; }
    public void setAtivo(Boolean ativo) { this.ativo = ativo; }
}