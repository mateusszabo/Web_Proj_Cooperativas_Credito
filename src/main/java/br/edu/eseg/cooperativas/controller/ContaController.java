package br.edu.eseg.cooperativas.controller;

import br.edu.eseg.cooperativas.domain.enums.CanalAtendimento;
import br.edu.eseg.cooperativas.domain.enums.StatusConta;
import br.edu.eseg.cooperativas.domain.enums.TipoConta;
import br.edu.eseg.cooperativas.domain.model.Conta;
import br.edu.eseg.cooperativas.service.AssociadoService;
import br.edu.eseg.cooperativas.service.ContaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;
    private final AssociadoService associadoService;

    public ContaController(ContaService contaService, AssociadoService associadoService) {
        this.contaService = contaService;
        this.associadoService = associadoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("contas", contaService.listarTodas());
        model.addAttribute("associados", associadoService.listarTodos());
        model.addAttribute("tiposConta", TipoConta.values());
        model.addAttribute("canais", CanalAtendimento.values());
        model.addAttribute("statusValores", StatusConta.values());
        model.addAttribute("novaConta", new Conta());
        return "contas/lista";
    }

    @PostMapping
    public String abrirConta(@ModelAttribute("novaConta") Conta conta,
                             @RequestParam("idAssociado") Long idAssociado,
                             RedirectAttributes redirectAttributes) {
        contaService.abrirConta(conta, idAssociado);
        redirectAttributes.addFlashAttribute("sucesso", "Conta bancária aberta com sucesso!");
        return "redirect:/contas";
    }

    @PostMapping("/{id}/status")
    public String alterarStatus(@PathVariable Long id,
                                @RequestParam("novoStatus") StatusConta novoStatus,
                                RedirectAttributes redirectAttributes) {
        contaService.alterarStatus(id, novoStatus);
        redirectAttributes.addFlashAttribute("sucesso", "Status da conta alterado para " + novoStatus);
        return "redirect:/contas";
    }
}