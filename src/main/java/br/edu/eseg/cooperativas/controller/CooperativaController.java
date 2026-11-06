package br.edu.eseg.cooperativas.controller;

import br.edu.eseg.cooperativas.domain.model.Cooperativa;
import br.edu.eseg.cooperativas.service.CooperativaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cooperativas")
public class CooperativaController {

    private final CooperativaService cooperativaService;

    public CooperativaController(CooperativaService cooperativaService) {
        this.cooperativaService = cooperativaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("cooperativas", cooperativaService.listarTodas());
        model.addAttribute("novaCooperativa", new Cooperativa());
        return "cooperativas/lista";
    }

    @GetMapping("/{id}")
    public String detalhar(@PathVariable Long id, Model model) {
        Cooperativa coop = cooperativaService.buscarPorId(id);
        model.addAttribute("cooperativa", coop);
        model.addAttribute("associadosAtivosCount", cooperativaService.contarAssociadosAtivos(id));
        model.addAttribute("carteiraAtiva", cooperativaService.calcularCarteiraAtiva(id));
        model.addAttribute("depositosAtivos", cooperativaService.calcularDepositosAtivos(id));
        model.addAttribute("indicadores", cooperativaService.listarIndicadoresPorCooperativa(id));
        return "cooperativas/detalhe";
    }

    @PostMapping
    @PreAuthorize("hasRole('GESTOR')")
    public String salvar(@Valid @ModelAttribute("novaCooperativa") Cooperativa cooperativa,
                         BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("erro", "Dados inválidos no cadastro da cooperativa.");
            return "redirect:/cooperativas";
        }
        cooperativaService.salvar(cooperativa);
        redirectAttributes.addFlashAttribute("sucesso", "Cooperativa salva com sucesso!");
        return "redirect:/cooperativas";
    }

    @PostMapping("/{id}/excluir")
    @PreAuthorize("hasRole('GESTOR')")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        cooperativaService.excluir(id);
        redirectAttributes.addFlashAttribute("sucesso", "Cooperativa excluída com sucesso.");
        return "redirect:/cooperativas";
    }
}