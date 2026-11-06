package br.edu.eseg.cooperativas.controller;

import br.edu.eseg.cooperativas.domain.enums.ProdutoCredito;
import br.edu.eseg.cooperativas.domain.enums.TipoGarantia;
import br.edu.eseg.cooperativas.domain.model.OperacaoCredito;
import br.edu.eseg.cooperativas.domain.model.RegistroInadimplencia;
import br.edu.eseg.cooperativas.service.AssociadoService;
import br.edu.eseg.cooperativas.service.OperacaoCreditoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
@RequestMapping("/operacoes-credito")
public class OperacaoCreditoController {

    private final OperacaoCreditoService operacaoCreditoService;
    private final AssociadoService associadoService;

    public OperacaoCreditoController(OperacaoCreditoService operacaoCreditoService, AssociadoService associadoService) {
        this.operacaoCreditoService = operacaoCreditoService;
        this.associadoService = associadoService;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("operacoes", operacaoCreditoService.listarTodas());
        model.addAttribute("associados", associadoService.listarTodos());
        model.addAttribute("produtos", ProdutoCredito.values());
        model.addAttribute("garantias", TipoGarantia.values());
        model.addAttribute("novaOperacao", new OperacaoCredito());
        model.addAttribute("novoApontamento", new RegistroInadimplencia());
        return "credito/lista";
    }

    @PostMapping
    public String contratar(@ModelAttribute("novaOperacao") OperacaoCredito operacao,
                            @RequestParam("idAssociado") Long idAssociado,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        boolean isGestor = authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_GESTOR"));

        OperacaoCredito salva = operacaoCreditoService.contratarOperacao(operacao, idAssociado, isGestor);
        redirectAttributes.addFlashAttribute("sucesso",
                String.format("Operação contratada! Parcela Price calculada: R$ %s | Risco: %s",
                        salva.getParcelaMensal(), salva.getRiscoCredito()));
        return "redirect:/operacoes-credito";
    }

    @PostMapping("/{id}/quitar")
    public String quitar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        operacaoCreditoService.quitarContrato(id);
        redirectAttributes.addFlashAttribute("sucesso", "Operação de crédito QUITADA com sucesso!");
        return "redirect:/operacoes-credito";
    }

    @PostMapping("/{id}/cancelar")
    public String cancelar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        operacaoCreditoService.cancelarContrato(id);
        redirectAttributes.addFlashAttribute("sucesso", "Operação de crédito CANCELADA.");
        return "redirect:/operacoes-credito";
    }

    @PostMapping("/{id}/renegociar")
    @PreAuthorize("hasRole('GESTOR')")
    public String renegociar(@PathVariable Long id,
                             @RequestParam("novoPrazoMeses") int novoPrazoMeses,
                             @RequestParam("novaTaxaMensal") BigDecimal novaTaxaMensal,
                             RedirectAttributes redirectAttributes) {
        OperacaoCredito op = operacaoCreditoService.renegociarContrato(id, novoPrazoMeses, novaTaxaMensal);
        redirectAttributes.addFlashAttribute("sucesso",
                "Contrato renegociado pelo Gestor! Nova parcela Price: R$ " + op.getParcelaMensal());
        return "redirect:/operacoes-credito";
    }

    @PostMapping("/{id}/inadimplencia")
    public String registrarInadimplencia(@PathVariable Long id,
                                         @ModelAttribute("novoApontamento") RegistroInadimplencia apontamento,
                                         RedirectAttributes redirectAttributes) {
        RegistroInadimplencia reg = operacaoCreditoService.registrarApontamentoInadimplencia(id, apontamento);
        redirectAttributes.addFlashAttribute("sucesso",
                String.format("Apontamento registrado! Faixa: %s | PCLD calculada: R$ %s",
                        reg.getFaixaAtraso().getDescricao(), reg.getProvisaoPerdaCredito()));
        return "redirect:/operacoes-credito";
    }
}