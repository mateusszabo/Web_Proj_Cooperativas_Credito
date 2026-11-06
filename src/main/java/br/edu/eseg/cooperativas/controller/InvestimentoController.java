package br.edu.eseg.cooperativas.controller;

import br.edu.eseg.cooperativas.domain.enums.LiquidezInvestimento;
import br.edu.eseg.cooperativas.domain.enums.ProdutoInvestimento;
import br.edu.eseg.cooperativas.domain.model.Investimento;
import br.edu.eseg.cooperativas.service.AssociadoService;
import br.edu.eseg.cooperativas.service.InvestimentoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/investimentos")
public class InvestimentoController {

    private final InvestimentoService investimentoService;
    private final AssociadoService associadoService;

    public InvestimentoController(InvestimentoService investimentoService, AssociadoService associadoService) {
        this.investimentoService = investimentoService;
        this.associadoService = associadoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("investimentos", investimentoService.listarTodos());
        model.addAttribute("associados", associadoService.listarTodos());
        model.addAttribute("produtos", ProdutoInvestimento.values());
        model.addAttribute("liquidezes", LiquidezInvestimento.values());
        model.addAttribute("novoInvestimento", new Investimento());
        return "investimentos/lista";
    }

    @PostMapping
    public String aplicar(@ModelAttribute("novoInvestimento") Investimento investimento,
                          @RequestParam("idAssociado") Long idAssociado,
                          RedirectAttributes redirectAttributes) {
        investimentoService.aplicar(investimento, idAssociado);
        redirectAttributes.addFlashAttribute("sucesso", "Aplicação financeira registrada com sucesso!");
        return "redirect:/investimentos";
    }

    @PostMapping("/{id}/resgatar")
    public String resgatar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        investimentoService.resgatar(id);
        redirectAttributes.addFlashAttribute("sucesso", "Investimento RESGATADO com sucesso!");
        return "redirect:/investimentos";
    }
}