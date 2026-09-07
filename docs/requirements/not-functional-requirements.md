# Requisitos Não Funcionais (RNF) - ChatConnect

Este documento detalha os Requisitos Não Funcionais (RNF) da plataforma **ChatConnect**, estabelecendo os critérios de qualidade técnica, arquitetura, segurança, desempenho e confiabilidade da aplicação.

---

## 1. Tabela Resumida de Requisitos Não Funcionais

| RNF | Categoria | Descrição |
|---|---|---|
| **RNF0001** | Segurança & Autenticação | O sistema deve utilizar autenticação stateless via tokens JWT emitidos pelo provedor de identidade Clerk, validados via JWKS e interceptadores dedicados tanto para HTTP quanto para WebSocket (STOMP CONNECT). |
| **RNF0002** | Confiabilidade & Transações | As operações de persistência e modificação de estado (chats, participantes, mensagens) devem ser executadas com garantia transacional (ACID) via `@Transactional` do Spring. |
| **RNF0003** | Integridade Concorrente | O sistema deve assegurar a consistência dos dados de participantes, chats e contadores de mensagens mesmo sob operações concorrentes simultâneas. |
| **RNF0004** | Identificação Única | O sistema deve adotar identificadores universais únicos no padrão UUID (v4) para todas as entidades principais (`User`, `Chat`, `ChatParticipant`, `Message`). |
| **RNF0005** | Integridade Referencial | O banco de dados relacional deve assegurar a integridade referencial entre usuários, chats, participantes e mensagens através de constraints e chaves estrangeiras. |
| **RNF0006** | Autorização & RBAC | O sistema deve implementar controle rigoroso de acesso baseado em papéis (`ADMIN` e `DEFAULT`), garantindo isolamento total entre salas de chat distintas. |
| **RNF0007** | Tratamento de Falhas | O sistema deve centralizar o tratamento de erros em controladores REST com `@RestControllerAdvice`, respondendo a falhas com o padrão unificado `ApiError` e códigos HTTP semânticos (400, 403, 404, 500). |
| **RNF0008** | Observabilidade & Logs | O sistema deve registrar eventos significativos, anomalias e erros operacionais através do framework SLF4J / Logback para auditoria e diagnóstico. |
| **RNF0009** | Arquitetura Hexagonal | A arquitetura da aplicação deve seguir estritamente o padrão de Portas e Adaptadores (Hexagonal Architecture), separando o Domínio, a Camada de Aplicação e os Adaptadores de Infraestrutura. |
| **RNF0010** | Independência de Domínio | As entidades e regras do núcleo de negócio devem ser mantidas como POJOs puros em Java, desacopladas de frameworks de persistência (JPA) e bibliotecas de transporte. |
| **RNF0011** | Comunicação em Tempo Real | O sistema deve implementar comunicação bidirecional de baixa latência em tempo real utilizando o protocolo STOMP sobre WebSocket com suporte a fallback via SockJS. |
| **RNF0012** | Roteamento de Mensagens | O sistema deve garantir o isolamento e direcionamento correto de tópicos públicos (`/topic/chat.{chatId}`) e filas privadas de usuários autenticados (`/user/queue/notifications`). |
| **RNF0013** | Desempenho & Latência | Operações de consulta e despacho de mensagens devem possuir baixa latência (< 100ms em condições típicas), viabilizando uma experiência de mensageria instantânea fluida. |
| **RNF0014** | Escalabilidade & Concorrência | A aplicação deve suportar múltiplos clientes conectados simultaneamente em sessões WebSocket ativas sem degradação de consistência ou consumo excessivo de recursos. |
| **RNF0015** | Persistência Relacional | Os dados de histórico de mensagens, chats e usuários devem ser armazenados de forma durável no SGBD PostgreSQL 16. |
| **RNF0016** | Atomicidade Composta | Operações compostas (ex: criação de chat com participantes iniciais, ou exclusão de chat com suas mensagens) devem ser concluídas integralmente ou totalmente revertidas em caso de falha. |
| **RNF0017** | Desacoplamento do Broker | As portas de saída para notificações (`MessageNotificationPort`, `ChatNotificationPort`) devem manter as regras de aplicação isoladas da tecnologia subjacente do broker STOMP. |
| **RNF0018** | Proteção de Credenciais | O sistema não deve armazenar credenciais ou senhas no banco de dados local; senhas são gerenciadas exclusivamente pelo Clerk e tokens trafegam protegidos via TLS/HTTPS/WSS. |
| **RNF0019** | Auditoria Temporal | As entidades de usuário e mensagens devem registrar timestamps precisos de criação e atualização em fuso horário padronizado (`OffsetDateTime` / UTC). |
| **RNF0020** | Estado Volátil em Memória (Redis) | O sistema deve utilizar Redis com pool Lettuce (`commons-pool2`) para o controle volátil de presença de usuários em salas, garantindo consultas de presença de altíssima performance. |
| **RNF0021** | Validação Declarativa | O sistema deve validar payloads de entrada utilizando Bean Validation (Jakarta Validation) com DTOs imutáveis (`record`), retornando erros 400 Bad Request detalhados. |
| **RNF0022** | Documentação Interativa de API | A API REST deve disponibilizar documentação automática no formato OpenAPI 3.0 acessível interativamente através do Swagger UI (`/swagger-ui/index.html`). |
| **RNF0023** | Versionamento de Banco de Dados | A evolução do esquema relacional do banco de dados deve ser gerenciada através de migrações versionadas com Flyway (`src/main/resources/db/migration`). |
| **RNF0024** | Conteinerização & Portabilidade | A aplicação e seus serviços dependentes (PostgreSQL e Redis) devem ser totalmente configuráveis e orquestráveis via Docker e Docker Compose, com healthchecks ativos. |

---

## 2. Detalhamento por Dimensão Técnica

### 2.1. Arquitetura de Software
- **Padrão Hexagonal (Ports & Adapters)**:
  - **Domínio**: Entidades puras (`Chat`, `Message`, `User`, `ChatParticipant`) e enums.
  - **Portas de Entrada (Input Ports)**: `ChatUseCase`, `ChatParticipantUseCase`, `MessageUseCase`, `ManagePresenceUseCase`, `SyncClerkUserUseCase`.
  - **Portas de Saída (Output Ports)**: `ChatRepository`, `ChatParticipantRepository`, `MessageRepository`, `PresenceStateOutputPort`, `MessageNotificationPort`, `ChatNotificationPort`.
  - **Adaptadores de Entrada**: Controladores REST Spring MVC e controladores STOMP WebSocket.
  - **Adaptadores de Saída**: Repositórios Spring Data JPA (PostgreSQL), adaptador Redis Lettuce e adaptadores de notificação SimpMessagingTemplate.

### 2.2. Segurança e Autorização
- **Autenticação Delegada**: Integração com Clerk via backend SDK oficial (`com.clerk:backend-api`).
- **Validação de Token HTTP**: O filtro `HttpAuthFilter` intercepta requisições HTTP e valida a assinatura do token JWT do Clerk antes de delegar para o Spring Security Context.
- **Validação de Token WebSocket**: O interceptor `WebSocketAuthInterceptor` inspeciona o comando STOMP `CONNECT`, validando o header nativo `Authorization: Bearer <token>`.
- **Prevenção de Injeção & Ataques**: Validação de parâmetros via UUIDs tipados, consultas parametrizadas pelo Spring Data JPA e ausência de senhas em repouso no banco local.

### 2.3. Desempenho e Armazenamento
- **PostgreSQL 16**: Armazena estado persistente estruturado com índices únicos (`clerk_user_id`, `email`, etc.).
- **Redis (Lettuce Pool)**: Armazena o conjunto de IDs de usuários com visualização ativa em cada chat (`presence:chat:{chatId}`). Isso permite calcular em memória quais participantes devem receber incremento de mensagens não lidas e notificação privada em frações de milissegundo.
- **Flyway**: Garante a reprodutibilidade e integridade das migrações em qualquer ambiente.
