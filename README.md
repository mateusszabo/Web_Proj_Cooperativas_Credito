Projeto Integrador — Sistema de Cooperativas de Crédito

Tema: Modernização de Sistema Legado e Arquitetura Full Stack

📋 Resumo do Relatório Técnico (1ª Entrega)

Este repositório contém os artefatos da 1ª Entrega do Projeto Integrador de Programação Web. O relatório técnico formaliza o diagnóstico e a modelagem evoluída do sistema de Cooperativas de Crédito com base nos seguintes tópicos:

Contextualização e Escopo: Apresentação do domínio de negócio e da arquitetura alvo (Java 21, Spring Boot, Spring Data JPA, Spring Security, Thymeleaf e Bootstrap).

Diagnóstico Crítico da Base Legada (Fase 2): Auditoria detalhada das 8 tabelas originais com a quantificação exata de 17 anomalias estruturais (violações de 3FN, chaves estrangeiras transitivas, inconsistências temporais e dados desatualizados).

Máquina de Estados e Regras de Negócio (Requisito 2.3): Modelagem de Enums fortes e transições controladas para Associados, Contas, Contratos de Crédito, Transações e Investimentos, impedindo estados inconsistentes.

Matriz de Rastreabilidade (Fase 1): Mapeamento completo ligando os problemas do legado aos requisitos revisados (RF01 a RF04), novos requisitos funcionais de valor agregado (RF05 a RF08) e requisitos não funcionais (RNF01 a RNF03).

Evolução do Modelo Relacional e Saneamento (Fase 4): Comparativo estrutural do Antes e Depois (Modelo Legado vs. Modelo Proposto em 3ª Forma Normal), estratégias de índices, restrições UNIQUE e rotina de saneamento ETL.

Modelagem Orientada a Objetos e Segurança (Fase 3): Especificação do Diagrama de Classes de Domínio, definição do lado proprietário (@JoinColumn) nas associações JPA e arquitetura de segurança RBAC com os perfis ROLE_ANALISTA e ROLE_GESTOR.

📁 Estrutura de Ficheiros (/docs)

Entrega_01_Projeto_Programacao_Web_Cooperativas_Credito.pdf — Relatório técnico completo.

Modelagem_Cooperativas_Credito.asta — Projeto oficial estruturado no Astah UML.

01_Modelo_Relacional_Legado.png — Diagrama ER da base legada original.

02_Diagrama_Classes_e_Modelo_Evoluido.png — Diagrama de classes e modelo relacional proposto (3FN).

17_cooperativas_credito-1.xlsx — Base de dados sintética original utilizada para o diagnóstico.

⚙️ Escopo da 2ª Entrega (Fases 5 a 8)

Implementação do backend executável em Spring Boot 3 (Java 21) com persistência no banco H2 e integração web:

Configuração e Persistência JPA (Fases 5 e 6): Mapeamento das 9 entidades em 3FN (domain), Enums de controle de estado, interfaces JpaRepository (repository) e carga inicial saneada da base legada (DataSeeder).

Regras de Negócio e Serviços (Fase 7): Validação de transições de status, consistência cronológica, cálculo automático de parcelas pela Tabela Price e provisão PCLD (BigDecimal), além do controle de alçada por comprometimento de renda (service).

Controllers, Rotas e Segurança (Fase 8): Fluxos HTTP MVC integrados ao Thymeleaf, tratamento global de exceções (GlobalExceptionHandler) e autenticação/autorização com os perfis ROLE_ANALISTA e ROLE_GESTOR (Spring Security).

▶️ Execução Rápida

Iniciar aplicação: Executar CooperativasCreditoApplication.java (ou mvn spring-boot:run) e aceder a http://localhost:8080/dashboard.

Credenciais de Acesso: Analista (analista / analista123) | Gestor (gestor / gestor123).

Console H2 (http://localhost:8080/h2-console): JDBC URL jdbc:h2:file:./database/cooperativas_db;AUTO_SERVER=TRUE (User: sa, sem senha).
