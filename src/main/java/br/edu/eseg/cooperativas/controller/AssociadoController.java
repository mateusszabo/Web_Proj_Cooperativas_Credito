package br.edu.eseg.cooperativas.controller;

import br.edu.eseg.cooperativas.domain.enums.FaixaIdade;
import br.edu.eseg.cooperativas.domain.enums.SegmentoAssociado;
import br.edu.eseg.cooperativas.domain.enums.StatusAssociado;
import br.edu.eseg.cooperativas.domain.model.Associado;
import br.edu.eseg.cooperativas.service.AssociadoService;
import br.edu.eseg.cooperativas.service.CooperativaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/associados")
public class AssociadoController {

    private final AssociadoService associadoService;
    private final CooperativaService cooperativaService;

    public AssociadoController(AssociadoService associadoService, CooperativaService cooperativaService) {
        this.associadoService = associadoService;
        this.cooperativaService = cooperativaService;
    }

    @GetMapping
    public String listar(Model model) {
        carregarDadosFormulario(model);
        model.addAttribute("associados", associadoService.listarTodos());
        model.addAttribute("associado", new Associado());
        return "associados/lista";
    }

    @GetMapping("/{id}/editar")
    public String exibirFormularioEdicao(@PathVariable Long id, Model model) {
        carregarDadosFormulario(model);
        model.addAttribute("associados", associadoService.listarTodos());
        model.addAttribute("associado", associadoService.buscarPorId(id));
        return "associados/lista";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("associado") Associado associado,
                         BindingResult result,
                         @RequestParam("idCooperativa") Long idCooperativa,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            carregarDadosFormulario(model);
            model.addAttribute("associados", associadoService.listarTodos());
            return "associados/lista";
        }
        associadoService.salvar(associado, idCooperativa);
        redirectAttributes.addFlashAttribute("sucesso", "Associado persistido com sucesso em 3FN!");
        return "redirect:/associados";
    }

    @PostMapping("/{id}/status")
    public String alterarStatus(@PathVariable Long id,
                                @RequestParam("novoStatus") StatusAssociado novoStatus,
                                RedirectAttributes redirectAttributes) {
        associadoService.alterarStatus(id, novoStatus);
        redirectAttributes.addFlashAttribute("sucesso", "Status do associado atualizado para " + novoStatus);
        return "redirect:/associados";
    }

    private void carregarDadosFormulario(Model model) {
        model.addAttribute("cooperativas", cooperativaService.listarTodas());
        model.addAttribute("segmentos", SegmentoAssociado.values());
        model.addAttribute("faixasIdade", FaixaIdade.values());
        model.addAttribute("statusValores", StatusAssociado.values());
    }
}