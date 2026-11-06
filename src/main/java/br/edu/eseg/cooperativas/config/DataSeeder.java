package br.edu.eseg.cooperativas.config;

import br.edu.eseg.cooperativas.domain.enums.*;
import br.edu.eseg.cooperativas.domain.model.*;
import br.edu.eseg.cooperativas.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UsuarioRepository usuarioRepository;
    private final CooperativaRepository cooperativaRepository;
    private final AssociadoRepository associadoRepository;
    private final ContaRepository contaRepository;
    private final OperacaoCreditoRepository operacaoCreditoRepository;
    private final RegistroInadimplenciaRepository inadimplenciaRepository;
    private final TransacaoRepository transacaoRepository;
    private final InvestimentoRepository investimentoRepository;
    private final IndicadorMensalRepository indicadorMensalRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UsuarioRepository usuarioRepository,
                      CooperativaRepository cooperativaRepository,
                      AssociadoRepository associadoRepository,
                      ContaRepository contaRepository,
                      OperacaoCreditoRepository operacaoCreditoRepository,
                      RegistroInadimplenciaRepository inadimplenciaRepository,
                      TransacaoRepository transacaoRepository,
                      InvestimentoRepository investimentoRepository,
                      IndicadorMensalRepository indicadorMensalRepository,
                      PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.cooperativaRepository = cooperativaRepository;
        this.associadoRepository = associadoRepository;
        this.contaRepository = contaRepository;
        this.operacaoCreditoRepository = operacaoCreditoRepository;
        this.inadimplenciaRepository = inadimplenciaRepository;
        this.transacaoRepository = transacaoRepository;
        this.investimentoRepository = investimentoRepository;
        this.indicadorMensalRepository = indicadorMensalRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (cooperativaRepository.count() > 0) {
            log.info("[DataSeeder RNF03] Base H2 já inicializada ({} cooperativas encontradas).", cooperativaRepository.count());
            return;
        }

        log.info("[DataSeeder RNF03] Iniciando saneamento e migração em 3FN da base legada 17_cooperativas_credito...");

        // 1. Usuarios para RBAC (RF08)
        usuarioRepository.saveAll(List.of(
                new Usuario("analista", passwordEncoder.encode("analista123"), "Analista de Atendimento", PerfilUsuario.ROLE_ANALISTA, true),
                new Usuario("gestor", passwordEncoder.encode("gestor123"), "Gestor de Alçada e Risco", PerfilUsuario.ROLE_GESTOR, true)
        ));

        // 2. As 10 Cooperativas reais da planilha legada (sem a coluna estática defasada associados_ativos)
        Cooperativa c1 = criarCooperativa("CoopCred Paulista", "São Paulo", "SP", -23.5505, -46.6333, 2011, 36, 1766, "1937826000.00", "30.44");
        Cooperativa c2 = criarCooperativa("União Financeira Cooperativa", "Campinas", "SP", -22.9099, -47.0626, 2002, 156, 486, "2773832000.00", "25.37");
        Cooperativa c3 = criarCooperativa("CredVale", "Curitiba", "PR", -25.4284, -49.2733, 1988, 15, 706, "1555361000.00", "16.01");
        Cooperativa c4 = criarCooperativa("AgroCred Brasil", "Porto Alegre", "RS", -30.0346, -51.2177, 1989, 4, 1974, "447040600.00", "25.56");
        Cooperativa c5 = criarCooperativa("CoopSul Crédito", "Belo Horizonte", "MG", -19.9167, -43.9345, 2019, 101, 1228, "432028500.00", "22.38");
        Cooperativa c6 = criarCooperativa("CredMais Regional", "Goiânia", "GO", -16.6869, -49.2648, 2020, 102, 872, "2074690000.00", "17.04");
        Cooperativa c7 = criarCooperativa("Cooperativa Capital", "Recife", "PE", -8.0476, -34.8770, 1989, 72, 3900, "1430466000.00", "20.95");
        Cooperativa c8 = criarCooperativa("CredNordeste", "Fortaleza", "CE", -3.7319, -38.5267, 2003, 125, 1040, "2632637000.00", "16.48");
        Cooperativa c9 = criarCooperativa("CoopCentro Financeira", "Brasília", "DF", -15.7939, -47.8828, 2017, 39, 350, "1202577000.00", "16.38");
        Cooperativa c10 = criarCooperativa("CredIntegra", "Ribeirão Preto", "SP", -21.1775, -47.8103, 1988, 54, 1381, "727532700.00", "20.22");
        cooperativaRepository.saveAll(List.of(c1, c2, c3, c4, c5, c6, c7, c8, c9, c10));

        // 3. Associados saneados (tempoAssociadoAnos reconciliado com a idade da cooperativa e faixa etária - RF03)
        Associado a1 = criarAssociado(c5, SegmentoAssociado.PRODUTOR_RURAL, "Ribeirão Preto", "SP", FaixaIdade.FAIXA_36_45, 6, "104005.30", 731, StatusAssociado.ATIVO);
        Associado a2 = criarAssociado(c1, SegmentoAssociado.MICROEMPRESA, "São Paulo", "SP", FaixaIdade.FAIXA_61_MAIS, 14, "58738.03", 766, StatusAssociado.ATIVO);
        Associado a3 = criarAssociado(c5, SegmentoAssociado.PROFISSIONAL_LIBERAL, "Belo Horizonte", "MG", FaixaIdade.FAIXA_46_60, 5, "33286.62", 426, StatusAssociado.ATIVO);
        Associado a4 = criarAssociado(c8, SegmentoAssociado.PROFISSIONAL_LIBERAL, "Fortaleza", "CE", FaixaIdade.FAIXA_46_60, 10, "28182.88", 803, StatusAssociado.INATIVO);
        Associado a5 = criarAssociado(c2, SegmentoAssociado.COOPERADO_INSTITUCIONAL, "Campinas", "SP", FaixaIdade.FAIXA_36_45, 4, "149974.52", 631, StatusAssociado.ATIVO);
        Associado a6 = criarAssociado(c9, SegmentoAssociado.PEQUENA_EMPRESA, "Brasília", "DF", FaixaIdade.FAIXA_61_MAIS, 6, "54569.50", 473, StatusAssociado.ATIVO);
        Associado a7 = criarAssociado(c4, SegmentoAssociado.PESSOA_FISICA, "Porto Alegre", "RS", FaixaIdade.FAIXA_26_35, 5, "14795.33", 886, StatusAssociado.ATIVO);
        Associado a8 = criarAssociado(c3, SegmentoAssociado.MICROEMPRESA, "Curitiba", "PR", FaixaIdade.FAIXA_61_MAIS, 12, "24829.08", 952, StatusAssociado.ATIVO);
        associadoRepository.saveAll(List.of(a1, a2, a3, a4, a5, a6, a7, a8));

        // 4. Contas reconciliadas em 3FN (pertencem exclusivamente ao Associado titular, sem id_cooperativa redundante)
        Conta ct1 = criarConta(a1, TipoConta.CONTA_CORRENTE, LocalDate.of(2024, 2, 10), "139409.62", "82693.59", "45.00", StatusConta.ATIVA, CanalAtendimento.AGENCIA);
        Conta ct2 = criarConta(a2, TipoConta.CONTA_DIGITAL, LocalDate.of(2024, 3, 13), "105904.31", "77557.10", "0.00", StatusConta.ATIVA, CanalAtendimento.APLICATIVO);
        Conta ct3 = criarConta(a3, TipoConta.CONTA_POUPANCA, LocalDate.of(2024, 4, 15), "128543.17", "35000.00", "0.00", StatusConta.ATIVA, CanalAtendimento.INTERNET_BANKING);
        Conta ct4 = criarConta(a4, TipoConta.CONTA_CORRENTE, LocalDate.of(2024, 1, 20), "0.00", "0.00", "0.00", StatusConta.ENCERRADA, CanalAtendimento.AGENCIA);
        Conta ct5 = criarConta(a5, TipoConta.CONTA_CORRENTE, LocalDate.of(2024, 6, 1), "248711.81", "254254.79", "47.72", StatusConta.ATIVA, CanalAtendimento.AGENCIA);
        Conta ct6 = criarConta(a6, TipoConta.CONTA_SALARIO, LocalDate.of(2025, 1, 15), "59588.75", "15000.00", "0.00", StatusConta.ATIVA, CanalAtendimento.APLICATIVO);
        contaRepository.saveAll(List.of(ct1, ct2, ct3, ct4, ct5, ct6));

        // 5. Operacoes de Credito com calculo real da Tabela Price (RF05)
        OperacaoCredito op1 = criarOperacao(a1, ProdutoCredito.FINANCIAMENTO_RURAL, LocalDate.of(2024, 5, 13), "350000.00", 60, "1.34", RiscoCredito.B, TipoGarantia.IMOVEL, StatusContrato.ATIVO, 0);
        OperacaoCredito op2 = criarOperacao(a2, ProdutoCredito.ANTECIPACAO_RECEBIVEIS, LocalDate.of(2024, 8, 11), "180000.00", 36, "2.05", RiscoCredito.A, TipoGarantia.APLICACAO_FINANCEIRA, StatusContrato.QUITADO, 0);
        OperacaoCredito op3 = criarOperacao(a3, ProdutoCredito.CAPITAL_DE_GIRO, LocalDate.of(2024, 10, 5), "95000.00", 48, "2.17", RiscoCredito.D, TipoGarantia.VEICULO, StatusContrato.EM_ATRASO, 40);
        OperacaoCredito op4 = criarOperacao(a5, ProdutoCredito.CREDITO_IMOBILIARIO, LocalDate.of(2025, 1, 20), "620000.00", 120, "1.16", RiscoCredito.B, TipoGarantia.IMOVEL, StatusContrato.ATIVO, 0);
        OperacaoCredito op5 = criarOperacao(a6, ProdutoCredito.CREDITO_PESSOAL, LocalDate.of(2024, 11, 10), "45000.00", 24, "2.45", RiscoCredito.D, TipoGarantia.AVAL, StatusContrato.EM_ATRASO, 65);
        operacaoCreditoRepository.saveAll(List.of(op1, op2, op3, op4, op5));

        // 6. Registros de Inadimplencia saneados (apenas em contratos EM_ATRASO e mesReferencia >= dataContratacao)
        RegistroInadimplencia in1 = criarInadimplencia(op3, "2025-02", "88450.00", "6420.00", 40, "28.50");
        RegistroInadimplencia in2 = criarInadimplencia(op5, "2025-03", "41200.00", "4980.00", 65, "42.00");
        inadimplenciaRepository.saveAll(List.of(in1, in2));

        // 7. Transacoes saneadas (dataHora >= dataAbertura da conta e somente sobre contas ATIVAS)
        Transacao t1 = criarTransacao(ct1, LocalDateTime.of(2025, 2, 14, 10, 30), TipoTransacao.PIX, CanalAtendimento.APLICATIVO, "4500.00", "0.00", StatusTransacao.CONCLUIDA, "Ribeirão Preto", "SP");
        Transacao t2 = criarTransacao(ct2, LocalDateTime.of(2025, 2, 18, 14, 15), TipoTransacao.BOLETO, CanalAtendimento.INTERNET_BANKING, "1250.80", "3.50", StatusTransacao.CONCLUIDA, "São Paulo", "SP");
        Transacao t3 = criarTransacao(ct5, LocalDateTime.of(2025, 3, 1, 9, 45), TipoTransacao.TED, CanalAtendimento.AGENCIA, "18900.00", "12.90", StatusTransacao.CONCLUIDA, "Campinas", "SP");
        Transacao t4 = criarTransacao(ct3, LocalDateTime.of(2025, 3, 5, 16, 20), TipoTransacao.DEPOSITO, CanalAtendimento.AGENCIA, "8000.00", "0.00", StatusTransacao.CONCLUIDA, "Belo Horizonte", "MG");
        transacaoRepository.saveAll(List.of(t1, t2, t3, t4));

        // 8. Investimentos vinculados diretamente ao Associado
        Investimento inv1 = criarInvestimento(a1, LocalDate.of(2024, 9, 25), ProdutoInvestimento.COTA_CAPITAL, "276107.85", "5.42", 323, LiquidezInvestimento.DIAS_30, StatusInvestimento.ATIVO);
        Investimento inv2 = criarInvestimento(a2, LocalDate.of(2024, 6, 10), ProdutoInvestimento.RDC, "150000.00", "11.25", 360, LiquidezInvestimento.DIARIA, StatusInvestimento.ATIVO);
        Investimento inv3 = criarInvestimento(a5, LocalDate.of(2024, 4, 1), ProdutoInvestimento.LCA, "320000.00", "10.80", 720, LiquidezInvestimento.VENCIMENTO, StatusInvestimento.ATIVO);
        investimentoRepository.saveAll(List.of(inv1, inv2, inv3));

        // 9. Indicadores Mensais com PK substituta e constraint unica (id_cooperativa, mes_referencia)
        IndicadorMensal ind1 = criarIndicador(c1, "2025-01", 806603, "4818817000.00", "8391119000.00", "27139927.45", "2.34", "68.91", "51.39", "3807054.24", "6.81");
        IndicadorMensal ind2 = criarIndicador(c2, "2025-01", 944049, "7361903000.00", "8485758000.00", "66810099.72", "2.02", "76.75", "53.25", "16163730.18", "8.39");
        indicadorMensalRepository.saveAll(List.of(ind1, ind2));

        log.info("[DataSeeder RNF03] Migração concluída com sucesso! 10 Cooperativas, {} Associados, {} Contas, {} Operações de Crédito (Tabela Price) e 2 Perfis RBAC ativos.",
                associadoRepository.count(), contaRepository.count(), operacaoCreditoRepository.count());
    }

    private Cooperativa criarCooperativa(String nome, String cidade, String uf, double lat, double lon,
                                         int fundacao, int agencias, int func, String patRef, String indCap) {
        Cooperativa c = new Cooperativa();
        c.setNome(nome);
        c.setCidade(cidade);
        c.setEstado(uf);
        c.setLatitude(lat);
        c.setLongitude(lon);
        c.setAnoFundacao(fundacao);
        c.setAgencias(agencias);
        c.setFuncionarios(func);
        c.setPatrimonioReferencia(new BigDecimal(patRef));
        c.setIndiceCapitalizacaoPercentual(new BigDecimal(indCap));
        return c;
    }

    private Associado criarAssociado(Cooperativa coop, SegmentoAssociado seg, String cidade, String uf,
                                     FaixaIdade faixa, int tempoAnos, String renda, int score, StatusAssociado status) {
        Associado a = new Associado();
        a.setCooperativa(coop);
        a.setSegmento(seg);
        a.setCidade(cidade);
        a.setEstado(uf);
        a.setIdadeFaixa(faixa);
        a.setTempoAssociadoAnos(tempoAnos);
        a.setRendaMensalEstimada(new BigDecimal(renda));
        a.setScoreCredito(score);
        a.setStatusAssociado(status);
        return a;
    }

    private Conta criarConta(Associado titular, TipoConta tipo, LocalDate abertura, String saldo,
                             String limite, String tarifa, StatusConta status, CanalAtendimento canal) {
        Conta c = new Conta();
        c.setAssociado(titular);
        c.setTipoConta(tipo);
        c.setDataAbertura(abertura);
        c.setSaldoMedioMensal(new BigDecimal(saldo));
        c.setLimiteCredito(new BigDecimal(limite));
        c.setTarifaMensal(new BigDecimal(tarifa));
        c.setStatusConta(status);
        c.setCanalAbertura(canal);
        return c;
    }

    private OperacaoCredito criarOperacao(Associado tomador, ProdutoCredito prod, LocalDate data, String valor,
                                          int prazo, String taxa, RiscoCredito risco, TipoGarantia garantia,
                                          StatusContrato status, int diasAtraso) {
        OperacaoCredito op = new OperacaoCredito();
        op.setAssociado(tomador);
        op.setProdutoCredito(prod);
        op.setDataContratacao(data);
        op.setValorContratado(new BigDecimal(valor));
        op.setPrazoMeses(prazo);
        op.setTaxaJurosMensalPercentual(new BigDecimal(taxa));
        op.calcularParcelaPrice();
        op.setRiscoCredito(risco);
        op.setGarantia(garantia);
        op.setStatusContrato(status);
        op.setDiasAtraso(diasAtraso);
        return op;
    }

    private RegistroInadimplencia criarInadimplencia(OperacaoCredito op, String mesRef, String saldoDev,
                                                     String valAtraso, int dias, String probDefault) {
        RegistroInadimplencia r = new RegistroInadimplencia();
        r.setOperacaoCredito(op);
        r.setMesReferencia(mesRef);
        r.setSaldoDevedor(new BigDecimal(saldoDev));
        r.setValorEmAtraso(new BigDecimal(valAtraso));
        r.setDiasAtraso(dias);
        r.setProbabilidadeDefaultPercentual(new BigDecimal(probDefault));
        r.calcularFaixaEProvisao();
        return r;
    }

    private Transacao criarTransacao(Conta conta, LocalDateTime dataHora, TipoTransacao tipo, CanalAtendimento canal,
                                     String valor, String tarifa, StatusTransacao status, String cidade, String uf) {
        Transacao t = new Transacao();
        t.setConta(conta);
        t.setDataHora(dataHora);
        t.setTipoTransacao(tipo);
        t.setCanal(canal);
        t.setValorTransacao(new BigDecimal(valor));
        t.setTarifaTransacao(new BigDecimal(tarifa));
        t.setStatusTransacao(status);
        t.setCidade(cidade);
        t.setEstado(uf);
        return t;
    }

    private Investimento criarInvestimento(Associado investidor, LocalDate data, ProdutoInvestimento prod,
                                           String valor, String rentAnual, int prazoDias,
                                           LiquidezInvestimento liq, StatusInvestimento status) {
        Investimento i = new Investimento();
        i.setAssociado(investidor);
        i.setDataAplicacao(data);
        i.setProdutoInvestimento(prod);
        i.setValorAplicado(new BigDecimal(valor));
        i.setRentabilidadeAnualPercentual(new BigDecimal(rentAnual));
        i.setPrazoDias(prazoDias);
        i.setLiquidez(liq);
        i.setStatusInvestimento(status);
        return i;
    }

    private IndicadorMensal criarIndicador(Cooperativa coop, String mesRef, int assocAtivos, String carteira,
                                           String depositos, String resultado, String inad, String efic,
                                           String dig, String recServ, String satisfacao) {
        IndicadorMensal ind = new IndicadorMensal();
        ind.setCooperativa(coop);
        ind.setMesReferencia(mesRef);
        ind.setAssociadosAtivos(assocAtivos);
        ind.setCarteiraCreditoTotal(new BigDecimal(carteira));
        ind.setDepositosTotais(new BigDecimal(depositos));
        ind.setResultadoLiquido(new BigDecimal(resultado));
        ind.setIndiceInadimplenciaPercentual(new BigDecimal(inad));
        ind.setIndiceEficienciaPercentual(new BigDecimal(efic));
        ind.setOperacoesDigitaisPercentual(new BigDecimal(dig));
        ind.setReceitaServicos(new BigDecimal(recServ));
        ind.setSatisfacaoAssociado(new BigDecimal(satisfacao));
        return ind;
    }
}