package br.edu.eseg.cooperativas.controller;

import br.edu.eseg.cooperativas.domain.enums.PerfilUsuario;
import br.edu.eseg.cooperativas.service.UsuarioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('GESTOR')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("perfis", PerfilUsuario.values());
        return "usuarios/lista";
    }

    @PostMapping
    public String cadastrar(@RequestParam("username") String username,
                            @RequestParam("senha") String senha,
                            @RequestParam("nomeCompleto") String nomeCompleto,
                            @RequestParam("perfil") PerfilUsuario perfil,
                            RedirectAttributes redirectAttributes) {
        usuarioService.cadastrarUsuario(username, senha, nomeCompleto, perfil);
        redirectAttributes.addFlashAttribute("sucesso", "Utilizador '" + username + "' criado com hash BCrypt!");
        return "redirect:/usuarios";
    }

    @PostMapping("/{id}/status")
    public String alternarStatus(@PathVariable Long id,
                                 Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        usuarioService.alternarStatusAtivo(id, auth.getName());
        redirectAttributes.addFlashAttribute("sucesso", "Status de acesso do utilizador atualizado.");
        return "redirect:/usuarios";
    }
}