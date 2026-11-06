package br.edu.eseg.cooperativas.service;

import br.edu.eseg.cooperativas.domain.enums.*;
import br.edu.eseg.cooperativas.domain.model.Associado;
import br.edu.eseg.cooperativas.domain.model.Conta;
import br.edu.eseg.cooperativas.domain.model.Transacao;
import br.edu.eseg.cooperativas.exception.RegraNegocioException;
import br.edu.eseg.cooperativas.repository.ContaRepository;
import br.edu.eseg.cooperativas.repository.TransacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final ContaRepository contaRepository;

    public TransacaoService(TransacaoRepository transacaoRepository, ContaRepository contaRepository) {
        this.transacaoRepository = transacaoRepository;
        this.contaRepository = contaRepository;
    }

    @Transactional(readOnly = true)
    public List<Transacao> listarTodas() {
        return transacaoRepository.listarTodasComContaEAssociado();
    }

    @Transactional(readOnly = true)
    public Transacao buscarPorId(Long idTransacao) {
        return transacaoRepository.findById(idTransacao)
                .orElseThrow(() -> new RegraNegocioException("Transação não encontrada com ID: " + idTransacao));
    }

    /**
     * RF02 e RF03: Registra e processa movimentacao financeira validando status da Conta/Associado
     * e impedindo datas anteriores a abertura da conta (8.055 anomalias na base legada).
     */
    @Transactional
    public Transacao realizarTransacao(Transacao transacao, Long idConta) {
        Conta conta = contaRepository.findById(idConta)
                .orElseThrow(() -> new RegraNegocioException("Conta bancária não encontrada."));
        Associado titular = conta.getAssociado();

        // RF02: Bloqueia movimentacao em conta BLOQUEADA/ENCERRADA ou associado nao ATIVO
        if (conta.getStatusConta() != StatusConta.ATIVA) {
            throw new RegraNegocioException(
                    "Transação rejeitada (RF02): a conta bancária encontra-se " + conta.getStatusConta());
        }
        if (titular.getStatusAssociado() != StatusAssociado.ATIVO) {
            throw new RegraNegocioException(
                    "Transação rejeitada (RF02): o associado titular encontra-se " + titular.getStatusAssociado());
        }

        if (transacao.getDataHora() == null) {
            transacao.setDataHora(LocalDateTime.now());
        }

        // RF03: Consistencia cronologica obrigatoria (dataTransacao >= conta.dataAbertura)
        if (transacao.getDataHora().toLocalDate().isBefore(conta.getDataAbertura())) {
            throw new RegraNegocioException(String.format(
                    "Inconsistência cronológica (RF03): a data da transação (%s) não pode ser anterior à abertura da conta (%s).",
                    transacao.getDataHora().toLocalDate(), conta.getDataAbertura()));
        }

        transacao.setConta(conta);
        if (transacao.getCidade() == null || transacao.getCidade().isBlank()) {
            transacao.setCidade(titular.getCidade());
            transacao.setEstado(titular.getEstado());
        }

        // Validacao de saldo + limite de credito para saidas financeiras
        BigDecimal custoTotal = transacao.getValorTransacao().add(
                transacao.getTarifaTransacao() != null ? transacao.getTarifaTransacao() : BigDecimal.ZERO);

        if (transacao.getTipoTransacao().isSaidaFinanceira()) {
            BigDecimal disponivelTotal = conta.getSaldoMedioMensal().add(conta.getLimiteCredito());
            if (custoTotal.compareTo(disponivelTotal) > 0) {
                transacao.setStatusTransacao(StatusTransacao.RECUSADA);
                return transacaoRepository.save(transacao);
            }
            conta.setSaldoMedioMensal(conta.getSaldoMedioMensal().subtract(custoTotal));
        } else {
            conta.setSaldoMedioMensal(conta.getSaldoMedioMensal().add(transacao.getValorTransacao()));
        }

        transacao.setStatusTransacao(StatusTransacao.CONCLUIDA);
        contaRepository.save(conta);
        return transacaoRepository.save(transacao);
    }

    /**
     * Requisito 2.3 e RF08: Estorno de transacao concluida (exclusivo para ROLE_GESTOR).
     * Transacoes nunca sao excluidas fisicamente para preservar a trilha de auditoria.
     */
    @Transactional
    public Transacao estornarTransacao(Long idTransacao) {
        Transacao transacao = buscarPorId(idTransacao);
        transacao.alterarStatus(StatusTransacao.ESTORNADA);

        Conta conta = transacao.getConta();
        BigDecimal valorTotal = transacao.getValorTransacao().add(transacao.getTarifaTransacao());
        if (transacao.getTipoTransacao().isSaidaFinanceira()) {
            conta.setSaldoMedioMensal(conta.getSaldoMedioMensal().add(valorTotal));
        } else {
            conta.setSaldoMedioMensal(conta.getSaldoMedioMensal().subtract(transacao.getValorTransacao()));
        }

        contaRepository.save(conta);
        return transacaoRepository.save(transacao);
    }
}