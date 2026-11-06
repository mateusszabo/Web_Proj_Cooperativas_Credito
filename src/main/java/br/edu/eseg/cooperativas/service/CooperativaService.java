package br.edu.eseg.cooperativas.service;

import br.edu.eseg.cooperativas.domain.model.Cooperativa;
import br.edu.eseg.cooperativas.domain.model.IndicadorMensal;
import br.edu.eseg.cooperativas.exception.RegraNegocioException;
import br.edu.eseg.cooperativas.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CooperativaService {

    private final CooperativaRepository cooperativaRepository;
    private final AssociadoRepository associadoRepository;
    private final ContaRepository contaRepository;
    private final OperacaoCreditoRepository operacaoCreditoRepository;
    private final IndicadorMensalRepository indicadorMensalRepository;

    public CooperativaService(CooperativaRepository cooperativaRepository,
                              AssociadoRepository associadoRepository,
                              ContaRepository contaRepository,
                              OperacaoCreditoRepository operacaoCreditoRepository,
                              IndicadorMensalRepository indicadorMensalRepository) {
        this.cooperativaRepository = cooperativaRepository;
        this.associadoRepository = associadoRepository;
        this.contaRepository = contaRepository;
        this.operacaoCreditoRepository = operacaoCreditoRepository;
        this.indicadorMensalRepository = indicadorMensalRepository;
    }

    @Transactional(readOnly = true)
    public List<Cooperativa> listarTodas() {
        return cooperativaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cooperativa buscarPorId(Long id) {
        return cooperativaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Cooperativa não encontrada com ID: " + id));
    }

    @Transactional
    public Cooperativa salvar(Cooperativa cooperativa) {
        cooperativaRepository.findByNomeIgnoreCase(cooperativa.getNome())
                .ifPresent(existente -> {
                    if (!existente.getIdCooperativa().equals(cooperativa.getIdCooperativa())) {
                        throw new RegraNegocioException("Já existe uma cooperativa cadastrada com o nome: " + cooperativa.getNome());
                    }
                });
        cooperativa.setEstado(cooperativa.getEstado().toUpperCase());
        return cooperativaRepository.save(cooperativa);
    }

    @Transactional
    public void excluir(Long idCooperativa) {
        Cooperativa coop = buscarPorId(idCooperativa);
        if (!associadoRepository.findByCooperativaIdCooperativa(idCooperativa).isEmpty()) {
            throw new RegraNegocioException(
                    "Exclusão bloqueada: a cooperativa possui associados vinculados. Remova ou transfira os associados antes.");
        }
        cooperativaRepository.delete(coop);
    }

    // RF07: Consultas agregadas em tempo real substituindo colunas estaticas defasadas
    @Transactional(readOnly = true)
    public long contarAssociadosAtivos(Long idCooperativa) {
        return associadoRepository.contarAssociadosAtivosPorCooperativa(idCooperativa);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularCarteiraAtiva(Long idCooperativa) {
        return operacaoCreditoRepository.somarCarteiraAtivaPorCooperativa(idCooperativa);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularDepositosAtivos(Long idCooperativa) {
        return contaRepository.somarDepositosAtivosPorCooperativa(idCooperativa);
    }

    @Transactional(readOnly = true)
    public List<IndicadorMensal> listarIndicadoresPorCooperativa(Long idCooperativa) {
        return indicadorMensalRepository.findByCooperativaIdCooperativaOrderByMesReferenciaDesc(idCooperativa);
    }
}