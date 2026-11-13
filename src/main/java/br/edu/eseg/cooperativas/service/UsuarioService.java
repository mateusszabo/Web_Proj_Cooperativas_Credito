package br.edu.eseg.cooperativas.service;

import br.edu.eseg.cooperativas.domain.enums.PerfilUsuario;
import br.edu.eseg.cooperativas.domain.model.Usuario;
import br.edu.eseg.cooperativas.exception.RegraNegocioException;
import br.edu.eseg.cooperativas.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario cadastrarUsuario(String username, String senhaPura, String nomeCompleto, PerfilUsuario perfil) {
        if (usuarioRepository.findByUsername(username.trim().toLowerCase()).isPresent()) {
            throw new RegraNegocioException("Já existe um utilizador registado com o username: " + username);
        }
        if (senhaPura == null || senhaPura.trim().length() < 6) {
            throw new RegraNegocioException("A senha deve possuir no mínimo 6 caracteres.");
        }
        Usuario novo = new Usuario(
                username.trim().toLowerCase(),
                passwordEncoder.encode(senhaPura),
                nomeCompleto.trim(),
                perfil,
                true
        );
        return usuarioRepository.save(novo);
    }

    @Transactional
    public void alternarStatusAtivo(Long idUsuario, String usernameLogado) {
        Usuario u = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RegraNegocioException("Utilizador não encontrado."));

        if (u.getUsername().equalsIgnoreCase(usernameLogado)) {
            throw new RegraNegocioException("Operação bloqueada: não é permitido desativar o próprio utilizador logado.");
        }
        u.setAtivo(!u.getAtivo());
        usuarioRepository.save(u);
    }
}