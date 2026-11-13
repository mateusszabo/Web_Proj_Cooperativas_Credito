# Projeto Integrador — Sistema de Cooperativas de Crédito
**Tema:** Modernização de Sistema Legado e Arquitetura Full Stack
---

## 📋 Resumo do Relatório Técnico (1ª Entrega — Fases 1 a 4)
Este repositório contém os artefatos da modernização do sistema de Cooperativas de Crédito com base nos seguintes tópicos:

1. **Contextualização e Escopo:** Apresentação do domínio de negócio e da arquitetura alvo (Java 21, Spring Boot, Spring Data JPA, Spring Security, Thymeleaf e Bootstrap).
2. **Diagnóstico Crítico da Base Legada (Fase 2):** Auditoria detalhada das 8 tabelas originais com a quantificação exata de 17 anomalias estruturais (violações de 3FN, chaves estrangeiras transitivas, inconsistências temporais e dados desatualizados).
3. **Máquina de Estados e Regras de Negócio (Requisito 2.3):** Modelagem de Enums fortes e transições controladas para Associados, Contas, Contratos de Crédito, Transações e Investimentos, impedindo estados inconsistentes.
4. **Matriz de Rastreabilidade (Fase 1):** Mapeamento completo ligando os problemas do legado aos requisitos revisados (`RF01` a `RF04`), novos requisitos funcionais de valor agregado (`RF05` a `RF08`) e requisitos não funcionais (`RNF01` a `RNF03`).
5. **Evolução do Modelo Relacional e Saneamento (Fase 4):** Comparativo estrutural do Antes e Depois (Modelo Legado vs. Modelo Proposto em 3ª Forma Normal), estratégias de índices, restrições UNIQUE e rotina de saneamento ETL.
6. **Modelagem Orientada a Objetos e Segurança (Fase 3):** Especificação do Diagrama de Classes de Domínio, definição do lado proprietário (`@JoinColumn`) nas associações JPA e arquitetura de segurança RBAC com os perfis `ROLE_ANALISTA` e `ROLE_GESTOR`.

---

## 🚀 Entregas 2 e 3: Implementação Backend e Interface Full Stack (Fases 5 a 10)
A aplicação executável foi desenvolvida cumprindo integralmente os requisitos de backend, persistência, segurança e interface:

- **Arquitetura & Persistência (Fases 5 a 8):** Configuração do Spring Boot 3, banco H2 (persistido em arquivo) e mapeamento das 9 entidades em 3FN. Implementação das regras de negócio (cálculo de Tabela Price, máquina de estados, validação de alçadas).
- **Saneamento e Migração (`DataSeeder`):** Rotina automática que carrega, higieniza e reconcilia os dados originais da planilha legada em 3FN na inicialização do sistema.
- **Autenticação e Autorização (Fase 9):** Implementação de Spring Security com bloqueio de recursos no servidor (`@PreAuthorize` / `SecurityFilterChain`) e ocultação de elementos na interface (`sec:authorize`).
- **Interface Responsiva (Fase 10):** Telas dinâmicas desenvolvidas com Thymeleaf, CSS e Bootstrap 5 (Login, Dashboard, Cooperativas, Associados, Contas, Operações de Crédito, Transações, Investimentos e Usuários RBAC).

### 🔑 Credenciais de Acesso (Segurança RBAC)
- **Perfil Gestor (Acesso total + aprovações de alçada):** `gestor` / `gestor123`
- **Perfil Analista (Acesso operacional restrito):** `analista` / `analista123`

### 🔧 Como Executar a Aplicação e os Testes
1. **Executar o sistema:** Rode a classe `CooperativasCreditoApplication.java` na IDE (ou via terminal: `mvn spring-boot:run`).
2. **Acesso ao Sistema Web:** [http://localhost:8080/login](http://localhost:8080/login)
3. **Console do Banco H2:** [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
    - *JDBC URL:* `jdbc:h2:file:./database/cooperativas_db;AUTO_SERVER=TRUE`
    - *User:* `sa` | *Senha:* (deixar em branco)
4. **Executar os Testes Automatizados (JUnit 5):** Execute `CooperativasRegrasNegocioTest.java` na IDE ou rode `mvn test` no terminal.

---

## ✅ 4ª Entrega: Integração Full Stack, Testes e Relatório Final (Fases 11 a 13)

### 1. Registro dos Testes Automatizados Executados (`CooperativasRegrasNegocioTest`)
A suíte automatizada (`src/test/java/br/edu/eseg/cooperativas/CooperativasRegrasNegocioTest.java`) verifica cenários válidos, inválidos, casos-limite e restrições de segurança:

- **`RF01` (Integridade 3FN):** Valida a navegação orientada a objetos (`Conta -> Associado -> Cooperativa`) sem chaves estrangeiras redundantes (**PASS**).
- **`RF02` (Prevenção de Cadastros Fantasmas):** Valida o bloqueio de abertura de conta para associado `INATIVO` e o bloqueio de inativação de associado que possua conta `ATIVA` (**PASS**).
- **`RF03` (Consistência Cronológica e Demográfica):** Valida o bloqueio de transação anterior à abertura da conta e o caso-limite demográfico de associado na faixa `18-25` com mais de 7 anos de vínculo (**PASS**).
- **`RF04` (Máquina de Estados):** Valida o bloqueio de lançamento de atraso/inadimplência sobre contratos em estado terminal `QUITADO` (**PASS**).
- **`RF05` (Motor Financeiro Price e PCLD):** Verifica o cálculo exato da prestação mensal pela Tabela Price (`R$ 28.387,57`) e o cálculo automático da faixa de atraso e provisão PCLD (`R$ 12.500,00`) com `BigDecimal` (**PASS**).
- **`RF06` (Política de Concessão de Crédito):** Valida o caso-limite de Score crítico (`< 400` / `Risco E`) bloqueando operação sem garantia real, além da trava de comprometimento de 35% da renda mensal (**PASS**).
- **`RF07` (Dashboard Dinâmico):** Comprova a atualização instantânea do saldo da carteira ativa da cooperativa após a quitação de um contrato (**PASS**).
- **`RF08` (Segurança RBAC com `MockMvc`):** Comprova o retorno `HTTP 403 Forbidden` quando `ROLE_ANALISTA` tenta acessar `/usuarios` ou estornar transações, e `HTTP 200 OK` para `ROLE_GESTOR` (**PASS**).

* **Funcionalidades não cobertas por testes unitários automatizados:** A renderização visual de estilização CSS/Bootstrap no navegador e a leitura direta do arquivo binário `.xlsx` (substituída por carga determinística higienizada no componente `DataSeeder`), validadas por inspeção visual e funcional em execução.

### 2. Limitações Conhecidas, Riscos Residuais e Melhorias Futuras (Item 5.4)
- **Limitações Conhecidas:** Uso do banco embarcado H2 em arquivo local (adequado para reprodutibilidade acadêmica, mas sem alta disponibilidade concorrente) e carga inicial focada em amostra saneada representativa para inicialização rápida.
- **Riscos Residuais:** A aprovação de operações com comprometimento de renda acima de 35% concentra-se na alçada única do perfil `ROLE_GESTOR`, sem exigência de dupla aprovação (*four-eyes principle*).
- **Melhorias Futuras:** Migração do banco para PostgreSQL gerenciado por Flyway, paginação server-side (`Pageable`) nas listagens de alto volume, trilha de auditoria com Hibernate Envers e integração de geração de boletos/Pix via API REST externa.

---

## 📁 Estrutura de Ficheiros (`/docs`)
- `Entrega_01_Projeto_Programacao_Web_Cooperativas_Credito.pdf` — Relatório técnico completo.
- `Modelagem_Cooperativas_Credito.asta` — Projeto oficial estruturado no Astah UML.
- `01_Modelo_Relacional_Legado.png` — Diagrama ER da base legada original.
- `02_Diagrama_Classes_e_Modelo_Evoluido.png` — Diagrama de classes e modelo relacional proposto (3FN).
- `17_cooperativas_credito-1.xlsx` — Base de dados sintética original utilizada para o diagnóstico.