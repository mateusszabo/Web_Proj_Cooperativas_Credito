package br.edu.eseg.cooperativas.service;

import br.edu.eseg.cooperativas.domain.enums.*;
import br.edu.eseg.cooperativas.domain.model.Associado;
import br.edu.eseg.cooperativas.domain.model.OperacaoCredito;
import br.edu.eseg.cooperativas.domain.model.RegistroInadimplencia;
import br.edu.eseg.cooperativas.exception.RegraNegocioException;
import br.edu.eseg.cooperativas.repository.AssociadoRepository;
import br.edu.eseg.cooperativas.repository.OperacaoCreditoRepository;
import br.edu.eseg.cooperativas.repository.RegistroInadimplenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class OperacaoCreditoService {

    private static final BigDecimal LIMITE_COMPROMETIMENTO_ANALISTA = new BigDecimal("0.35"); // 35% da renda

    private final OperacaoCreditoRepository operacaoCreditoRepository;
    private final AssociadoRepository associadoRepository;
    private final RegistroInadimplenciaRepository inadimplenciaRepository;

    public OperacaoCreditoService(OperacaoCreditoRepository operacaoCreditoRepository,
                                  AssociadoRepository associadoRepository,
                                  RegistroInadimplenciaRepository inadimplenciaRepository) {
        this.operacaoCreditoRepository = operacaoCreditoRepository;
        this.associadoRepository = associadoRepository;
        this.inadimplenciaRepository = inadimplenciaRepository;
    }

    @Transactional(readOnly = true)
    public List<OperacaoCredito> listarTodas() {
        return operacaoCreditoRepository.listarTodasComAssociadoECooperativa();
    }

    @Transactional(readOnly = true)
    public OperacaoCredito buscarPorId(Long idOperacao) {
        return operacaoCreditoRepository.findById(idOperacao)
                .orElseThrow(() -> new RegraNegocioException("Operação de crédito não encontrada com ID: " + idOperacao));
    }

    /**
     * RF02, RF05, RF06 e RF08: Contratacao de credito com calculo automatico da Tabela Price,
     * classificacao automatica de Risco (AA a E) e trava de comprometimento de 35% da renda mensal.
     */
    @Transactional
    public OperacaoCredito contratarOperacao(OperacaoCredito novaOperacao, Long idAssociado, boolean usuarioPossuiAlcadaGestor) {
        Associado tomador = associadoRepository.findById(idAssociado)
                .orElseThrow(() -> new RegraNegocioException("Associado tomador não encontrado."));

        // RF02: Impede os 754 casos legados de contratos ativos para associados inativos
        if (tomador.getStatusAssociado() != StatusAssociado.ATIVO) {
            throw new RegraNegocioException(
                    "Concessão bloqueada (RF02): apenas associados com status ATIVO podem contratar crédito.");
        }

        if (novaOperacao.getDataContratacao() == null) {
            novaOperacao.setDataContratacao(LocalDate.now());
        }

        // RF06: Classificacao automatica de risco pelo Score do associado
        RiscoCredito riscoCalculado = RiscoCredito.classificarPorScore(tomador.getScoreCredito());
        novaOperacao.setRiscoCredito(riscoCalculado);

        // RF06: Score critico (< 400 / Risco E) exige garantia real obrigatoria
        if (riscoCalculado == RiscoCredito.E && novaOperacao.getGarantia() == TipoGarantia.SEM_GARANTIA) {
            throw new RegraNegocioException(
                    "Política de Crédito (RF06): associados com Score inferior a 400 (Risco E) não podem contratar operações sem garantia.");
        }

        // RF05: Calculo automatico da prestacao mensal pela Tabela Price (Sistema Frances de Amortizacao)
        BigDecimal parcelaPrice = novaOperacao.calcularParcelaPrice();

        // RF06 e RF08: Verificacao de comprometimento de renda (parcelas ativas + nova parcela <= 35% da renda)
        BigDecimal parcelasAtuais = operacaoCreditoRepository.somarParcelasAtivasDoAssociado(idAssociado);
        BigDecimal comprometimentoTotal = parcelasAtuais.add(parcelaPrice);
        BigDecimal tetoAnalista = tomador.getRendaMensalEstimada()
                .multiply(LIMITE_COMPROMETIMENTO_ANALISTA, MathContext.DECIMAL64)
                .setScale(2, RoundingMode.HALF_UP);

        if (comprometimentoTotal.compareTo(tetoAnalista) > 0 && !usuarioPossuiAlcadaGestor) {
            throw new RegraNegocioException(String.format(
                    "Alçada excedida (RF06/RF08): o comprometimento mensal total (R$ %s) ultrapassa 35%% da renda do associado (Limite Analista: R$ %s). Exige aprovação de ROLE_GESTOR.",
                    comprometimentoTotal, tetoAnalista));
        }

        novaOperacao.setAssociado(tomador);
        novaOperacao.setDiasAtraso(0);
        novaOperacao.setStatusContrato(StatusContrato.ATIVO);
        return operacaoCreditoRepository.save(novaOperacao);
    }

    @Transactional
    public OperacaoCredito quitarContrato(Long idOperacao) {
        OperacaoCredito op = buscarPorId(idOperacao);
        op.alterarStatus(StatusContrato.QUITADO);
        return operacaoCreditoRepository.save(op);
    }

    @Transactional
    public OperacaoCredito cancelarContrato(Long idOperacao) {
        OperacaoCredito op = buscarPorId(idOperacao);
        op.alterarStatus(StatusContrato.CANCELADO);
        return operacaoCreditoRepository.save(op);
    }

    /**
     * RF04 e RF08: Renegociacao de contrato em atraso (exclusivo para ROLE_GESTOR).
     */
    @Transactional
    public OperacaoCredito renegociarContrato(Long idOperacao, int novoPrazoMeses, BigDecimal novaTaxaMensal) {
        OperacaoCredito op = buscarPorId(idOperacao);
        if (op.getStatusContrato() != StatusContrato.EM_ATRASO) {
            throw new RegraNegocioException("Apenas contratos no status EM_ATRASO podem ser renegociados.");
        }
        op.setPrazoMeses(novoPrazoMeses);
        op.setTaxaJurosMensalPercentual(novaTaxaMensal);
        op.calcularParcelaPrice();
        op.alterarStatus(StatusContrato.RENEGOCIADO);
        return operacaoCreditoRepository.save(op);
    }

    /**
     * RF03, RF04 e RF05: Registra apontamento mensal de inadimplencia com validacao cronologica,
     * bloqueio em contratos quitados/cancelados, constraint de unicidade mensal e calculo de PCLD.
     */
    @Transactional
    public RegistroInadimplencia registrarApontamentoInadimplencia(Long idOperacao, RegistroInadimplencia apontamento) {
        OperacaoCredito op = buscarPorId(idOperacao);

        // RF04: Bloqueia os 2.078 casos legados de atraso lancado em contrato QUITADO ou CANCELADO
        if (op.getStatusContrato().isTerminal()) {
            throw new RegraNegocioException(
                    "Operação bloqueada (RF04): contratos QUITADOS ou CANCELADOS não podem receber apontamentos de inadimplência.");
        }

        // RF03: Impede os 3.008 casos legados de inadimplencia anterior a contratacao do credito
        YearMonth mesRef = YearMonth.parse(apontamento.getMesReferencia());
        YearMonth mesContratacao = YearMonth.from(op.getDataContratacao());
        if (mesRef.isBefore(mesContratacao)) {
            throw new RegraNegocioException(String.format(
                    "Inconsistência cronológica (RF03): o mês de referência (%s) é anterior ao mês de contratação da operação (%s).",
                    mesRef, mesContratacao));
        }

        // RF04: Impede os 156 casos legados de duplicidade de apontamento no mesmo mes
        if (inadimplenciaRepository.existsByOperacaoCreditoIdOperacaoAndMesReferencia(idOperacao, apontamento.getMesReferencia())) {
            throw new RegraNegocioException(
                    "Duplicidade bloqueada (RF04): já existe apontamento de inadimplência para esta operação na competência "
                            + apontamento.getMesReferencia());
        }

        apontamento.setOperacaoCredito(op);
        apontamento.calcularFaixaEProvisao(); // RF05: Calcula PCLD automaticamente

        // Sincroniza diasAtraso e statusContrato da operacao principal (Secao 6.2)
        op.atualizarDiasAtraso(apontamento.getDiasAtraso());
        operacaoCreditoRepository.save(op);

        return inadimplenciaRepository.save(apontamento);
    }
}