package br.edu.eseg.cooperativas.service;

import br.edu.eseg.cooperativas.domain.enums.StatusAssociado;
import br.edu.eseg.cooperativas.domain.model.Associado;
import br.edu.eseg.cooperativas.domain.model.Cooperativa;
import br.edu.eseg.cooperativas.exception.RegraNegocioException;
import br.edu.eseg.cooperativas.repository.AssociadoRepository;
import br.edu.eseg.cooperativas.repository.CooperativaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssociadoService {

    private final AssociadoRepository associadoRepository;
    private final CooperativaRepository cooperativaRepository;

    public AssociadoService(AssociadoRepository associadoRepository, CooperativaRepository cooperativaRepository) {
        this.associadoRepository = associadoRepository;
        this.cooperativaRepository = cooperativaRepository;
    }

    @Transactional(readOnly = true)
    public List<Associado> listarTodos() {
        return associadoRepository.listarTodosComCooperativa();
    }

    @Transactional(readOnly = true)
    public Associado buscarPorId(Long id) {
        return associadoRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException("Associado não encontrado com ID: " + id));
    }

    /**
     * RF03: Impede as inconsistencias temporais e demograficas detectadas na auditoria da Fase 2
     * (1.711 casos de tempoAssociado > idadeCooperativa e 842 casos de adesao na infancia).
     */
    @Transactional
    public Associado salvar(Associado associado, Long idCooperativa) {
        Cooperativa coop = cooperativaRepository.findById(idCooperativa)
                .orElseThrow(() -> new RegraNegocioException("Cooperativa informada não existe."));
        associado.setCooperativa(coop);

        int idadeCooperativa = coop.getIdadeCooperativaAnos();
        if (associado.getTempoAssociadoAnos() > idadeCooperativa) {
            throw new RegraNegocioException(String.format(
                    "Inconsistência temporal (RF03): o tempo de associação (%d anos) não pode superar a idade da cooperativa %s (%d anos).",
                    associado.getTempoAssociadoAnos(), coop.getNome(), idadeCooperativa));
        }

        if (associado.getIdadeFaixa().name().equals("FAIXA_18_25") && associado.getTempoAssociadoAnos() > 7) {
            throw new RegraNegocioException(
                    "Inconsistência demográfica (RF03): associado na faixa 18-25 anos não pode ter mais de 7 anos de vínculo.");
        }

        associado.setEstado(associado.getEstado().toUpperCase());
        return associadoRepository.save(associado);
    }

    @Transactional
    public Associado alterarStatus(Long idAssociado, StatusAssociado novoStatus) {
        Associado associado = buscarPorId(idAssociado);
        associado.alterarStatus(novoStatus);
        return associadoRepository.save(associado);
    }
}