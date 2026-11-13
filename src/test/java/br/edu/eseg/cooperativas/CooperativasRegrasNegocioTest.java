package br.edu.eseg.cooperativas;

import br.edu.eseg.cooperativas.domain.enums.*;
import br.edu.eseg.cooperativas.domain.model.*;
import br.edu.eseg.cooperativas.exception.RegraNegocioException;
import br.edu.eseg.cooperativas.repository.*;
import br.edu.eseg.cooperativas.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CooperativasRegrasNegocioTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CooperativaService cooperativaService;

    @Autowired
    private AssociadoService associadoService;

    @Autowired
    private ContaService contaService;

    @Autowired
    private OperacaoCreditoService operacaoCreditoService;

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private AssociadoRepository associadoRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private OperacaoCreditoRepository operacaoCreditoRepository;

    @Test
    @DisplayName("RF01 (Cenário Válido): Deve garantir integridade em 3FN derivando a cooperativa da Conta pelo Associado titular")
    void deveGarantirIntegridade3FNEntreTransacaoContaAssociadoECooperativa() {
        Conta conta = contaRepository.listarTodasComAssociadoECooperativa().getFirst();
        assertNotNull(conta.getAssociado());
        assertNotNull(conta.getAssociado().getCooperativa());
        assertNotNull(conta.getAssociado().getCooperativa().getNome());
    }

    @Test
    @DisplayName("RF02 (Cenário Inválido): Deve bloquear abertura de conta para Associado INATIVO")
    void deveBloquearAberturaDeContaParaAssociadoInativo() {
        Associado inativo = associadoRepository.findAll().stream()
                .filter(a -> a.getStatusAssociado() == StatusAssociado.INATIVO)
                .findFirst()
                .orElseThrow();

        Conta novaConta = new Conta();
        novaConta.setTipoConta(TipoConta.CONTA_CORRENTE);
        novaConta.setCanalAbertura(CanalAtendimento.AGENCIA);
        novaConta.setDataAbertura(LocalDate.now());

        assertThrows(RegraNegocioException.class,
                () -> contaService.abrirConta(novaConta, inativo.getIdAssociado()));
    }

    @Test
    @DisplayName("RF02 (Cenário Inválido): Deve bloquear inativação de Associado que possui Conta ATIVA")
    void deveBloquearInativacaoDeAssociadoComContaAtiva() {
        Conta contaAtiva = contaRepository.findAll().stream()
                .filter(c -> c.getStatusConta() == StatusConta.ATIVA)
                .findFirst()
                .orElseThrow();

        Long idAssociado = contaAtiva.getAssociado().getIdAssociado();
        assertThrows(RegraNegocioException.class,
                () -> associadoService.alterarStatus(idAssociado, StatusAssociado.INATIVO));
    }

    @Test
    @DisplayName("RF03 (Cenário Inválido): Deve bloquear transação com data anterior à abertura da conta")
    void deveBloquearTransacaoAnteriorAAberturaDaConta() {
        Conta contaAtiva = contaRepository.findAll().stream()
                .filter(c -> c.getStatusConta() == StatusConta.ATIVA)
                .findFirst()
                .orElseThrow();

        Transacao retroativa = new Transacao();
        retroativa.setTipoTransacao(TipoTransacao.PIX);
        retroativa.setCanal(CanalAtendimento.APLICATIVO);
        retroativa.setValorTransacao(new BigDecimal("500.00"));
        retroativa.setDataHora(contaAtiva.getDataAbertura().minusDays(5).atTime(10, 0));

        assertThrows(RegraNegocioException.class,
                () -> transacaoService.realizarTransacao(retroativa, contaAtiva.getIdConta()));
    }

    @Test
    @DisplayName("RF03 (Caso-Limite Demográfico): Deve bloquear associado de 18-25 anos com mais de 7 anos de vínculo")
    void deveBloquearInconsistenciaDemograficaNoCadastroDeAssociado() {
        Cooperativa coop = cooperativaService.listarTodas().getFirst();

        Associado invalido = new Associado();
        invalido.setSegmento(SegmentoAssociado.PESSOA_FISICA);
        invalido.setCidade("São Paulo");
        invalido.setEstado("SP");
        invalido.setIdadeFaixa(FaixaIdade.FAIXA_18_25);
        invalido.setTempoAssociadoAnos(10); // Inválido para faixa 18-25 (adesão quando criança)
        invalido.setRendaMensalEstimada(new BigDecimal("4500.00"));
        invalido.setScoreCredito(720);

        assertThrows(RegraNegocioException.class,
                () -> associadoService.salvar(invalido, coop.getIdCooperativa()));
    }

    @Test
    @DisplayName("RF04 (Máquina de Estados): Deve bloquear apontamento de atraso em contrato QUITADO")
    void deveBloquearInadimplenciaEmContratoQuitado() {
        OperacaoCredito opQuitada = operacaoCreditoRepository.findAll().stream()
                .filter(op -> op.getStatusContrato() == StatusContrato.QUITADO)
                .findFirst()
                .orElseThrow();

        RegistroInadimplencia apontamento = new RegistroInadimplencia();
        apontamento.setMesReferencia("2025-05");
        apontamento.setSaldoDevedor(new BigDecimal("10000.00"));
        apontamento.setValorEmAtraso(new BigDecimal("1000.00"));
        apontamento.setDiasAtraso(30);
        apontamento.setProbabilidadeDefaultPercentual(new BigDecimal("15.00"));

        assertThrows(RegraNegocioException.class,
                () -> operacaoCreditoService.registrarApontamentoInadimplencia(opQuitada.getIdOperacao(), apontamento));
    }

    @Test
    @DisplayName("RF05 (Cenário Válido): Deve calcular a Parcela Mensal pela Tabela Price e a PCLD com precisão BigDecimal")
    void deveCalcularParcelaTabelaPriceEPcldComExatidao() {
        OperacaoCredito op = new OperacaoCredito();
        op.setValorContratado(new BigDecimal("1683868.69"));
        op.setPrazoMeses(119);
        op.setTaxaJurosMensalPercentual(new BigDecimal("1.34"));

        BigDecimal parcela = op.calcularParcelaPrice();
        assertEquals(new BigDecimal("28387.57"), parcela);

        RegistroInadimplencia reg = new RegistroInadimplencia();
        reg.setSaldoDevedor(new BigDecimal("50000.00"));
        reg.setValorEmAtraso(new BigDecimal("5000.00"));
        reg.setDiasAtraso(45);
        reg.setProbabilidadeDefaultPercentual(new BigDecimal("25.00"));
        reg.calcularFaixaEProvisao();

        assertEquals(FaixaAtraso.DE_31_A_60_DIAS, reg.getFaixaAtraso());
        assertEquals(new BigDecimal("12500.00"), reg.getProvisaoPerdaCredito());
    }

    @Test
    @DisplayName("RF06 (Caso-Limite de Score): Deve bloquear operação SEM_GARANTIA para Score < 400 (Risco E)")
    void deveBloquearOperacaoSemGarantiaParaScoreCritico() {
        Cooperativa coop = cooperativaService.listarTodas().getFirst();
        Associado scoreBaixo = new Associado();
        scoreBaixo.setSegmento(SegmentoAssociado.PESSOA_FISICA);
        scoreBaixo.setCidade("Campinas");
        scoreBaixo.setEstado("SP");
        scoreBaixo.setIdadeFaixa(FaixaIdade.FAIXA_36_45);
        scoreBaixo.setTempoAssociadoAnos(2);
        scoreBaixo.setRendaMensalEstimada(new BigDecimal("15000.00"));
        scoreBaixo.setScoreCredito(350); // Risco E (< 400)
        Associado salvo = associadoService.salvar(scoreBaixo, coop.getIdCooperativa());

        OperacaoCredito opSemGarantia = new OperacaoCredito();
        opSemGarantia.setProdutoCredito(ProdutoCredito.CREDITO_PESSOAL);
        opSemGarantia.setGarantia(TipoGarantia.SEM_GARANTIA);
        opSemGarantia.setValorContratado(new BigDecimal("5000.00"));
        opSemGarantia.setPrazoMeses(12);
        opSemGarantia.setTaxaJurosMensalPercentual(new BigDecimal("2.00"));

        assertThrows(RegraNegocioException.class,
                () -> operacaoCreditoService.contratarOperacao(opSemGarantia, salvo.getIdAssociado(), true));
    }

    @Test
    @DisplayName("RF06 e RF08 (Alçada 35%): Deve bloquear contratação por ANALISTA quando parcela supera 35% da renda e permitir para GESTOR")
    void deveValidarAlcadaDeComprometimentoDeRenda() {
        Associado ativo = associadoRepository.findAll().stream()
                .filter(a -> a.getStatusAssociado() == StatusAssociado.ATIVO)
                .findFirst()
                .orElseThrow();

        OperacaoCredito opAcimaAlcada = new OperacaoCredito();
        opAcimaAlcada.setProdutoCredito(ProdutoCredito.CREDITO_IMOBILIARIO);
        opAcimaAlcada.setGarantia(TipoGarantia.IMOVEL);
        opAcimaAlcada.setValorContratado(new BigDecimal("5000000.00"));
        opAcimaAlcada.setPrazoMeses(12);
        opAcimaAlcada.setTaxaJurosMensalPercentual(new BigDecimal("2.00"));
        opAcimaAlcada.setDataContratacao(LocalDate.now());

        // Analista (false) deve sofrer bloqueio de alçada
        assertThrows(RegraNegocioException.class,
                () -> operacaoCreditoService.contratarOperacao(opAcimaAlcada, ativo.getIdAssociado(), false));

        // Gestor (true) possui alçada superior para aprovar
        OperacaoCredito aprovada = operacaoCreditoService.contratarOperacao(opAcimaAlcada, ativo.getIdAssociado(), true);
        assertNotNull(aprovada.getIdOperacao());
        assertEquals(StatusContrato.ATIVO, aprovada.getStatusContrato());
    }

    @Test
    @DisplayName("RF07 (Painel Dinâmico): Deve atualizar imediatamente a carteira ativa ao quitar operação de crédito")
    void deveAtualizarCarteiraAtivaAoQuitarOperacao() {
        OperacaoCredito opAtiva = operacaoCreditoRepository.findAll().stream()
                .filter(op -> op.getStatusContrato() == StatusContrato.ATIVO)
                .findFirst()
                .orElseThrow();

        Long idCoop = opAtiva.getAssociado().getCooperativa().getIdCooperativa();
        BigDecimal carteiraAntes = cooperativaService.calcularCarteiraAtiva(idCoop);

        operacaoCreditoService.quitarContrato(opAtiva.getIdOperacao());

        BigDecimal carteiraDepois = cooperativaService.calcularCarteiraAtiva(idCoop);
        assertEquals(carteiraAntes.subtract(opAtiva.getValorContratado()), carteiraDepois);
    }

    @Test
    @WithMockUser(username = "analista", roles = {"ANALISTA"})
    @DisplayName("RF08 (Segurança HTTP 403): Deve bloquear acesso de ROLE_ANALISTA a rotas exclusivas de ROLE_GESTOR")
    void deveRetornarForbiddenParaAnalistaEmRotasDeGestor() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/transacoes/1/estornar").with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "gestor", roles = {"GESTOR"})
    @DisplayName("RF08 (Segurança RBAC): Deve permitir acesso de ROLE_GESTOR ao painel de usuários e dashboard")
    void devePermitirAcessoDeGestorARotasAdministrativas() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk());
    }
}