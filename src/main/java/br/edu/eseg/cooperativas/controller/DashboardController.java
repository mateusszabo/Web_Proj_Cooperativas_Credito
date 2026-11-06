package br.edu.eseg.cooperativas.controller;

import br.edu.eseg.cooperativas.domain.enums.StatusAssociado;
import br.edu.eseg.cooperativas.domain.enums.StatusContrato;
import br.edu.eseg.cooperativas.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final CooperativaRepository cooperativaRepository;
    private final AssociadoRepository associadoRepository;
    private final ContaRepository contaRepository;
    private final OperacaoCreditoRepository operacaoCreditoRepository;
    private final RegistroInadimplenciaRepository inadimplenciaRepository;
    private final InvestimentoRepository investimentoRepository;

    public DashboardController(CooperativaRepository cooperativaRepository,
                               AssociadoRepository associadoRepository,
                               ContaRepository contaRepository,
                               OperacaoCreditoRepository operacaoCreditoRepository,
                               RegistroInadimplenciaRepository inadimplenciaRepository,
                               InvestimentoRepository investimentoRepository) {
        this.cooperativaRepository = cooperativaRepository;
        this.associadoRepository = associadoRepository;
        this.contaRepository = contaRepository;
        this.operacaoCreditoRepository = operacaoCreditoRepository;
        this.inadimplenciaRepository = inadimplenciaRepository;
        this.investimentoRepository = investimentoRepository;
    }

    @GetMapping({"/", "/dashboard"})
    public String exibirDashboard(Model model) {
        model.addAttribute("totalCooperativas", cooperativaRepository.count());
        model.addAttribute("associadosAtivos", associadoRepository.countByStatusAssociado(StatusAssociado.ATIVO));
        model.addAttribute("saldoContasAtivas", contaRepository.somarSaldoContasAtivas());
        model.addAttribute("carteiraCreditoAtiva", operacaoCreditoRepository.somarCarteiraCreditoAtivaGlobal());
        model.addAttribute("contratosEmAtraso", operacaoCreditoRepository.countByStatusContrato(StatusContrato.EM_ATRASO));
        model.addAttribute("provisaoPerdaTotal", inadimplenciaRepository.somarProvisaoPerdaTotal());
        model.addAttribute("totalInvestimentosAtivos", investimentoRepository.somarInvestimentosAtivos());
        model.addAttribute("cooperativas", cooperativaRepository.findAll());
        return "dashboard";
    }
}