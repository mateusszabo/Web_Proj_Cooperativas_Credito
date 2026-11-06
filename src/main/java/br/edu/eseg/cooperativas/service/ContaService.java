package br.edu.eseg.cooperativas.service;

import br.edu.eseg.cooperativas.domain.enums.StatusAssociado;
import br.edu.eseg.cooperativas.domain.enums.StatusConta;
import br.edu.eseg.cooperativas.domain.enums.TipoConta;
import br.edu.eseg.cooperativas.domain.model.Associado;
import br.edu.eseg.cooperativas.domain.model.Conta;
import br.edu.eseg.cooperativas.exception.RegraNegocioException;
import br.edu.eseg.cooperativas.repository.AssociadoRepository;
import br.edu.eseg.cooperativas.repository.ContaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class ContaService {

    private final ContaRepository contaRepository;
    private final AssociadoRepository associadoRepository;

    public ContaService(ContaRepository contaRepository, AssociadoRepository associadoRepository) {
        this.contaRepository = contaRepository;
        this.associadoRepository = associadoRepository;
    }

    @Transactional(readOnly = true)
    public List<Conta> listarTodas() {
        return contaRepository.listarTodasComAssociadoECooperativa();
    }

    @Transactional(readOnly = true)
    public Conta buscarPorId(Long idConta) {
        return contaRepository.findById(idConta)
                .orElseThrow(() -> new RegraNegocioException("Conta bancária não encontrada com ID: " + idConta));
    }

    @Transactional
    public Conta abrirConta(Conta conta, Long idAssociado) {
        Associado titular = associadoRepository.findById(idAssociado)
                .orElseThrow(() -> new RegraNegocioException("Associado titular não encontrado."));

        // RF02: Bloqueia abertura de conta para associado INATIVO ou SUSPENSO (elimina os 1.336 casos legados)
        if (titular.getStatusAssociado() != StatusAssociado.ATIVO) {
            throw new RegraNegocioException(
                    "Operação negada (RF02): não é permitido abrir conta para associado com status " + titular.getStatusAssociado());
        }

        if (conta.getDataAbertura() == null) {
            conta.setDataAbertura(LocalDate.now());
        }
        if (conta.getDataAbertura().isAfter(LocalDate.now())) {
            throw new RegraNegocioException("A data de abertura da conta não pode ser futura.");
        }

        // Regra de dominio: Conta Salario e Conta Digital possuem isencao de tarifa mensal
        if (conta.getTipoConta() == TipoConta.CONTA_SALARIO || conta.getTipoConta() == TipoConta.CONTA_DIGITAL) {
            conta.setTarifaMensal(BigDecimal.ZERO);
        }

        conta.setAssociado(titular);
        conta.setStatusConta(StatusConta.ATIVA);
        return contaRepository.save(conta);
    }

    @Transactional
    public Conta alterarStatus(Long idConta, StatusConta novoStatus) {
        Conta conta = buscarPorId(idConta);
        conta.alterarStatus(novoStatus);
        return contaRepository.save(conta);
    }
}