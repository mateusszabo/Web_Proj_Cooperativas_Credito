package br.edu.eseg.cooperativas.service;

import br.edu.eseg.cooperativas.domain.enums.StatusAssociado;
import br.edu.eseg.cooperativas.domain.enums.StatusInvestimento;
import br.edu.eseg.cooperativas.domain.model.Associado;
import br.edu.eseg.cooperativas.domain.model.Investimento;
import br.edu.eseg.cooperativas.exception.RegraNegocioException;
import br.edu.eseg.cooperativas.repository.AssociadoRepository;
import br.edu.eseg.cooperativas.repository.InvestimentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class InvestimentoService {

    private final InvestimentoRepository investimentoRepository;
    private final AssociadoRepository associadoRepository;

    public InvestimentoService(InvestimentoRepository investimentoRepository, AssociadoRepository associadoRepository) {
        this.investimentoRepository = investimentoRepository;
        this.associadoRepository = associadoRepository;
    }

    @Transactional(readOnly = true)
    public List<Investimento> listarTodos() {
        return investimentoRepository.listarTodosComAssociado();
    }

    @Transactional
    public Investimento aplicar(Investimento investimento, Long idAssociado) {
        Associado investidor = associadoRepository.findById(idAssociado)
                .orElseThrow(() -> new RegraNegocioException("Associado investidor não encontrado."));

        if (investidor.getStatusAssociado() != StatusAssociado.ATIVO) {
            throw new RegraNegocioException("Apenas associados ATIVOS podem realizar novas aplicações financeiras.");
        }
        if (investimento.getDataAplicacao() == null) {
            investimento.setDataAplicacao(LocalDate.now());
        }
        investimento.setAssociado(investidor);
        investimento.setStatusInvestimento(StatusInvestimento.ATIVO);
        return investimentoRepository.save(investimento);
    }

    @Transactional
    public Investimento resgatar(Long idInvestimento) {
        Investimento inv = investimentoRepository.findById(idInvestimento)
                .orElseThrow(() -> new RegraNegocioException("Investimento não encontrado com ID: " + idInvestimento));
        inv.resgatar(LocalDate.now());
        return investimentoRepository.save(inv);
    }
}