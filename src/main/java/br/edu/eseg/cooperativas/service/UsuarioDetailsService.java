package br.edu.eseg.cooperativas.service;

import br.edu.eseg.cooperativas.domain.model.Usuario;
import br.edu.eseg.cooperativas.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + username));

        return new User(
                usuario.getUsername(),
                usuario.getSenhaHash(),
                usuario.getAtivo(),
                true,
                true,
                true,
                List.of(new SimpleGrantedAuthority(usuario.getPerfil().name()))
        );
    }
}