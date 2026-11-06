package br.edu.eseg.cooperativas.controller;

import br.edu.eseg.cooperativas.domain.enums.CanalAtendimento;
import br.edu.eseg.cooperativas.domain.enums.TipoTransacao;
import br.edu.eseg.cooperativas.domain.model.Transacao;
import br.edu.eseg.cooperativas.service.ContaService;
import br.edu.eseg.cooperativas.service.TransacaoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoService transacaoService;
    private final ContaService contaService;

    public TransacaoController(TransacaoService transacaoService, ContaService contaService) {
        this.transacaoService = transacaoService;
        this.contaService = contaService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("transacoes", transacaoService.listarTodas());
        model.addAttribute("contas", contaService.listarTodas());
        model.addAttribute("tiposTransacao", TipoTransacao.values());
        model.addAttribute("canais", CanalAtendimento.values());
        model.addAttribute("novaTransacao", new Transacao());
        return "transacoes/lista";
    }

    @PostMapping
    public String registrar(@ModelAttribute("novaTransacao") Transacao transacao,
                            @RequestParam("idConta") Long idConta,
                            RedirectAttributes redirectAttributes) {
        Transacao processada = transacaoService.realizarTransacao(transacao, idConta);
        redirectAttributes.addFlashAttribute("sucesso",
                "Transação processada com status: " + processada.getStatusTransacao());
        return "redirect:/transacoes";
    }

    @PostMapping("/{id}/estornar")
    @PreAuthorize("hasRole('GESTOR')")
    public String estornar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        transacaoService.estornarTransacao(id);
        redirectAttributes.addFlashAttribute("sucesso", "Transação ESTORNADA pelo Gestor e saldo recomposto.");
        return "redirect:/transacoes";
    }
}