# 💬 ChatConnect API

<div align="center">

![Java](https://img.shields.io/badge/Java-17-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.0-brightgreen.svg)
![Spring Security](https://img.shields.io/badge/Spring%20Security-Clerk%20JWT-blue.svg)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)
![Redis](https://img.shields.io/badge/Redis-Presence%20%26%20Cache-red.svg)
![WebSocket](https://img.shields.io/badge/WebSocket-STOMP%20%2F%20SockJS-yellow.svg)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED.svg)
![OpenAPI](https://img.shields.io/badge/OpenAPI-Swagger%203.0-green.svg)

Backend robusto, escalável e de alta performance para comunicação em tempo real, suportando chats privados (1-on-1) e em grupo, rastreamento de presença em memória com Redis, notificações STOMP e integração completa com **Clerk**.

</div>

---

## 📋 Índice

- [Visão Geral](#-visão-geral)
- [Arquitetura](#-arquitetura)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Funcionalidades Principais](#-funcionalidades-principais)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Requisitos e Regras de Negócio](#-requisitos-e-regras-de-negócio)
- [Como Executar](#-como-executar)
  - [Pré-requisitos](#pré-requisitos)
  - [Variáveis de Ambiente](#variáveis-de-ambiente)
  - [Executando via Docker Compose](#executando-via-docker-compose-recomendado)
  - [Executando Localmente](#executando-localmente)
- [Documentação da API REST (Swagger)](#-documentação-da-api-rest-swagger)
- [Guia Rápido de WebSocket & STOMP](#-guia-rápido-de-websocket--stomp)
- [Testes Automatizados](#-testes-automatizados)

---

## 🌟 Visão Geral

O **ChatConnect** é uma API moderna para mensageria instantânea projetada para suportar tanto operações síncronas (REST) quanto assíncronas bidirecionais (STOMP sobre WebSocket).

A solução resolve desafios complexos de sistemas de chat:
- **Gestão de Identidade Externa**: Autenticação delegada para o **Clerk**, eliminando a necessidade de armazenar senhas na base local e sincronizando usuários em tempo real através de webhooks seguros.
- **Rastreamento de Presença Instantâneo**: Utilização de conjuntos em memória no **Redis** para registrar quais usuários estão com a sala de chat aberta, permitindo entregar mensagens sem poluição de notificações e calcular com exatidão badges de mensagens não lidas.
- **Controle de Acesso Baseado em Papéis (RBAC)**: Diferenciação entre administradores (`ADMIN`) e membros comuns (`DEFAULT`) em grupos, com regras estritas de proteção e promoção automática de liderança.

---

## 🏛 Arquitetura

O sistema adota **Arquitetura Hexagonal (Ports and Adapters)** combinada com princípios de **Clean Architecture**, isolando completamente as regras de negócio das tecnologias de banco de dados, mensageria e autenticação.

```
                  ┌─────────────────────────────────────────┐
                  │          Adaptadores de Entrada         │
                  │   - REST Controllers (Spring Web MVC)   │
                  │   - WebSocket Controllers (STOMP)       │
                  │   - Clerk Webhooks Controller           │
                  └────────────────────┬────────────────────┘
                                       │
                                       ▼
                  ┌─────────────────────────────────────────┐
                  │       Portas de Entrada (Input)         │
                  │   - ChatUseCase                         │
                  │   - ChatParticipantUseCase              │
                  │   - MessageUseCase                      │
                  │   - ManagePresenceUseCase               │
                  │   - SyncClerkUserUseCase                │
                  └────────────────────┬────────────────────┘
                                       │
                                       ▼
                  ┌─────────────────────────────────────────┐
                  │           Camada de Aplicação           │
                  │   - ChatService / MessageService        │
                  │   - ChatParticipantService              │
                  │   - ManagePresenceService / UserService │
                  └────────────────────┬────────────────────┘
                                       │
                                       ▼
                  ┌─────────────────────────────────────────┐
                  │             Núcleo de Domínio           │
                  │   - Chat, Message, ChatParticipant      │
                  │   - User, ChatTypeEnum, Roles           │
                  │   (POJOs puros - Sem acoplamento a ORM) │
                  └────────────────────┬────────────────────┘
                                       │
                                       ▼
                  ┌─────────────────────────────────────────┐
                  │        Portas de Saída (Output)         │
                  │   - Repositórios de Domínio             │
                  │   - PresenceStateOutputPort             │
                  │   - MessageNotificationPort             │
                  │   - ChatNotificationPort                │
                  └────────────────────┬────────────────────┘
                                       │
                                       ▼
                  ┌─────────────────────────────────────────┐
                  │          Adaptadores de Saída           │
                  │   - PostgreSQL / Spring Data JPA        │
                  │   - Redis (Lettuce Connection Pool)     │
                  │   - SimpMessagingTemplate (STOMP Push)  │
                  └─────────────────────────────────────────┘
```

---

## 🚀 Tecnologias Utilizadas

- **Linguagem**: Java 17
- **Framework Principal**: Spring Boot 4.1.0
  - Spring Web MVC
  - Spring Security & OAuth2 Resource Server
  - Spring Data JPA
  - Spring WebSocket & STOMP Messaging
  - Spring Data Redis
  - Spring Validation (Jakarta Bean Validation)
- **Autenticação & Identidade**: [Clerk](https://clerk.com/) (SDK Oficial `com.clerk:backend-api:6.0.0` e validação via JWKS)
- **Banco de Dados Relacional**: PostgreSQL 16
- **Versionamento de Banco de Dados**: Flyway Database Migrations
- **Cache & Presença em Memória**: Redis (com pool de conexões `commons-pool2` / Lettuce)
- **Documentação de API**: SpringDoc OpenAPI 3.0 / Swagger UI
- **Utilitários**: Project Lombok, Jackson
- **Infraestrutura**: Docker & Docker Compose
- **Testes**: JUnit 5, Mockito, H2 Database

---

## ⚡ Funcionalidades Principais

### 1. Autenticação & Identidade (Clerk)
- Autenticação stateless via token JWT emitido pelo Clerk.
- Verificação de token em todas as rotas privadas REST e durante o handshake STOMP `CONNECT`.
- Sincronização automática de dados via Webhooks:
  - `POST /api/webhooks/clerk/user-created`: Criação e persistência do usuário local.
  - `POST /api/webhooks/clerk/user-updated`: Atualização de e-mail e username.
  - `POST /api/webhooks/clerk/user-deleted`: Exclusão física do usuário.

### 2. Gestão de Chats
- **Chat Privado (1-on-1)**:
  - Exatamente 2 participantes distintos.
  - Título dinâmico: cada participante visualiza o nome do outro interlocutor.
- **Chat em Grupo**:
  - Múltiplos participantes, título e descrição customizáveis.
  - Criador recebe o papel de `ADMIN`.
  - Administradores podem atualizar informações, convidar novos membros ou excluir o grupo.
  - Exclusão do chat remove em cascata todas as mensagens e participantes associados.

### 3. Gestão de Participantes & Permissões (RBAC)
- Papéis suportados: `ADMIN` e `DEFAULT`.
- Adição em lote de novos membros por administradores em grupos.
- Remoção controlada: administradores não podem remover a si próprios nem a outros administradores.
- Sucessão de liderança: caso o último administrador saia voluntariamente, o participante mais antigo herda a role `ADMIN`.
- Se um chat ficar sem nenhum participante, ele é automaticamente excluído.

### 4. Mensageria Híbrida (REST + WebSocket STOMP)
- **Envio**: Via REST (`POST /api/chat/{chatId}/messages`) ou STOMP (`/app/chat.sendMessage`).
- **Edição**: Permitida exclusivamente ao autor da mensagem via REST (`PATCH`) ou STOMP (`/app/chat.editMessage`).
- **Exclusão**: Permitida ao autor da mensagem ou administradores da sala via REST (`DELETE`) ou STOMP (`/app/chat.deleteMessage`).
- **Histórico**: Consulta completa de mensagens ordenadas por data via `GET /api/chat/{chatId}/messages`.

### 5. Presença em Tempo Real & Mensagens Não Lidas (Redis)
- Ao abrir o chat (subscrição em `/topic/chat.{chatId}`), a presença ativa é registrada no Redis e o contador de não lidas (`unreadMessages`) é zerado.
- Ao enviar uma mensagem:
  - Participantes ativos na sala recebem o evento em `/topic/chat.{chatId}`.
  - Participantes inativos têm o contador `unreadMessages` incrementado no PostgreSQL e recebem notificação pessoal em `/user/queue/notifications`.

---

## 📂 Estrutura do Projeto

```
api/
├── docs/                                 # Documentação do projeto
│   ├── diagrams/                         # Diagramas UML (Casos de Uso, DER)
│   │   ├── use-case-diagram.puml
│   │   ├── use-case-diagram.png
│   │   └── erd.png
│   ├── requirements/                     # Engenharia de Requisitos
│   │   ├── business-policies.md          # Regras de Negócio (RN)
│   │   ├── functional-requirements.md    # Requisitos Funcionais (RF)
│   │   └── not-functional-requirements.md# Requisitos Não Funcionais (RNF)
│   └── websocket.md                      # Manual completo do protocolo WebSocket/STOMP
├── src/
│   ├── main/
│   │   ├── java/com/example/chatConnectSpring/
│   │   │   ├── chat/                     # Bounded Context de Chats e Mensagens
│   │   │   │   ├── application/          # Casos de uso, services, commands e DTOs
│   │   │   │   ├── domain/               # Entidades de domínio puras e Portas (in/out)
│   │   │   │   └── infrastructure/       # Adaptadores HTTP, WebSocket, JPA e Redis
│   │   │   ├── user/                     # Bounded Context de Usuários e Identidade
│   │   │   │   ├── application/          # Serviços de sincronização e consulta
│   │   │   │   ├── domain/               # Modelo de domínio de Usuário e Portas
│   │   │   │   └── infrastructure/       # Webhooks do Clerk, JPA e DTOs
│   │   │   └── shared/                   # Configurações globais, segurança e exceções
│   │   │       ├── config/               # Security, WebSocket, Redis, OpenAPI, Clerk
│   │   │       ├── exception/            # GlobalExceptionHandler e ApiError
│   │   │       └── security/             # HttpAuthFilter e WebSocketAuthInterceptor
│   │   └── resources/
│   │       ├── application.properties    # Propriedades da aplicação
│   │       └── db/migration/             # Scripts versionados de migração Flyway
│   └── test/                             # Testes unitários e de integração
├── docker-compose.yml                    # Orquestração do ambiente completo
├── Dockerfile                            # Build multi-stage da aplicação Java
├── pom.xml                               # Dependências e plugins Maven
└── README.md                             # Documentação principal
```

---

## 📑 Requisitos e Regras de Negócio

Para consultar a especificação completa de requisitos do sistema:

- 📌 **[Regras de Negócio (RN)](docs/requirements/business-policies.md)**: 26 regras detalhadas que garantem a consistência do sistema.
- 📌 **[Requisitos Funcionais (RF)](docs/requirements/functional-requirements.md)**: 23 requisitos funcionais cobrindo todos os casos de uso.
- 📌 **[Requisitos Não Funcionais (RNF)](docs/requirements/not-functional-requirements.md)**: 24 requisitos de segurança, desempenho, arquitetura e infraestrutura.
- 📌 **[Diagrama de Casos de Uso (PlantUML)](docs/diagrams/use-case-diagram.puml)**: Mapeamento visual completo dos atores e casos de uso.

---

## 🛠 Como Executar

### Pré-requisitos
- **Java 17** (JDK) instalado
- **Maven 3.9+** (ou utilizar o wrapper `./mvnw`)
- **Docker** e **Docker Compose** instalados
- Uma conta ativa no **[Clerk](https://clerk.com/)** com chave de API (`CLERK_SECRET_KEY`)

---

### Variáveis de Ambiente

Crie um arquivo `.env` na raiz do projeto `api/` baseado nas seguintes variáveis:

```properties
# Banco de Dados PostgreSQL
DATABASE_URL=jdbc:postgresql://localhost:5432/chatconnectdatabase
DATABASE_USER=postgres
DATABASE_PASSWORD=senha123

# Cache e Presença Redis
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=senha123

# Autenticação Clerk
CLERK_SECRET_KEY=sk_test_sua_chave_secreta_aqui
```

> **Nota**: Ao executar via Docker Compose, substitua os hosts `localhost` por `postgres` e `redis`.

---

### Executando via Docker Compose (Recomendado)

O arquivo `docker-compose.yml` já configura a aplicação Spring Boot, o banco PostgreSQL 16 e o servidor Redis com suas devidas checagens de saúde (*healthchecks*).

```bash
# Iniciar todos os serviços em segundo plano
docker compose up -d

# Visualizar os logs da aplicação
docker compose logs -f api

# Para parar todos os containers
docker compose down
```

A API estará disponível em `http://localhost:8080`.

---

### Executando Localmente

Se preferir rodar a aplicação Spring Boot diretamente na máquina host:

1. **Suba os serviços de banco e cache:**
   ```bash
   docker compose up -d postgres redis
   ```

2. **Execute a aplicação via Maven Wrapper:**
   - **Linux / macOS:**
     ```bash
     ./mvnw spring-boot:run
     ```
   - **Windows (PowerShell):**
     ```powershell
     .\mvnw.cmd spring-boot:run
     ```

---

## 📖 Documentação da API REST (Swagger)

A API possui documentação OpenAPI 3.0 interativa disponível enquanto a aplicação estiver em execução:

- **Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Resumo dos Principais Endpoints

| Método | Endpoint | Descrição | Autenticação |
|---|---|---|---|
| `POST` | `/api/chat/group` | Criar chat em grupo | Requer Bearer Token |
| `POST` | `/api/chat/private` | Criar chat privado entre 2 usuários | Requer Bearer Token |
| `GET` | `/api/chat` | Listar chats do usuário autenticado | Requer Bearer Token |
| `GET` | `/api/chat/{chatId}` | Obter detalhes de um chat | Requer Bearer Token |
| `PATCH` | `/api/chat/{chatId}` | Atualizar título/descrição do grupo (`ADMIN`) | Requer Bearer Token |
| `DELETE` | `/api/chat/{chatId}` | Excluir chat e histórico (`ADMIN`) | Requer Bearer Token |
| `POST` | `/api/chat/{chatId}/participants` | Adicionar membros ao grupo (`ADMIN`) | Requer Bearer Token |
| `DELETE` | `/api/chat/{chatId}/participants/{id}` | Remover membro do grupo (`ADMIN`) | Requer Bearer Token |
| `POST` | `/api/chat/{chatId}/messages` | Enviar mensagem para o chat | Requer Bearer Token |
| `GET` | `/api/chat/{chatId}/messages` | Consultar histórico de mensagens | Requer Bearer Token |
| `PATCH` | `/api/chat/{chatId}/messages/{id}` | Editar mensagem (apenas o autor) | Requer Bearer Token |
| `DELETE` | `/api/chat/{chatId}/messages/{id}` | Excluir mensagem (autor ou `ADMIN`) | Requer Bearer Token |
| `POST` | `/api/webhooks/clerk/user-created` | Webhook de criação de usuário do Clerk | Pública |
| `POST` | `/api/webhooks/clerk/user-updated` | Webhook de atualização de usuário | Pública |
| `POST` | `/api/webhooks/clerk/user-deleted` | Webhook de exclusão de usuário | Pública |

---

## 📡 Guia Rápido de WebSocket & STOMP

Consulte a documentação completa em **[`docs/websocket.md`](docs/websocket.md)**.

### Conexão e Handshake
- **Endpoint**: `ws://localhost:8080/ws` (ou via SockJS em `http://localhost:8080/ws`)
- **Autenticação**: Envie o header nativo `Authorization: Bearer <CLERK_JWT_TOKEN>` no frame STOMP `CONNECT`.

### Comandos do Cliente (Envio)
- **Enviar Mensagem**: Destino `/app/chat.sendMessage`
  ```json
  { "chatId": "UUID", "content": "Olá, pessoal!" }
  ```
- **Editar Mensagem**: Destino `/app/chat.editMessage`
  ```json
  { "messageId": "UUID", "chatId": "UUID", "content": "Mensagem corrigida" }
  ```
- **Excluir Mensagem**: Destino `/app/chat.deleteMessage`
  ```json
  { "messageId": "UUID" }
  ```

### Inscrições (Recebimento)
- `/topic/chat.{chatId}`: Inscreva-se quando estiver visualizando a conversa. Recebe eventos de novas mensagens, edições e exclusões, e marca o usuário como ativo na sala via Redis.
- `/user/queue/notifications`: Inscreva-se assim que o usuário fizer login. Recebe notificações de novas mensagens para chats que não estão em visualização e avisos de entrada/saída de chats (`JOINED_CHAT`, `LEAVED_CHAT`).

---

## 🧪 Testes Automatizados

O projeto possui suíte de testes unitários cobrindo as regras de negócio dos serviços de aplicação:

```bash
# Executar todos os testes
./mvnw test

# Executar suíte de testes específica
./mvnw test -Dtest=ChatServiceTest
./mvnw test -Dtest=MessageServiceTest
./mvnw test -Dtest=ChatParticipantServiceTest
./mvnw test -Dtest=ManagePresenceServiceTest
```

---

## 📄 Licença

Este projeto é desenvolvido para fins de demonstração e integração de arquiteturas distribuídas com Spring Boot, Redis e Clerk. Distribuído sob os termos da licença livre de sua organização.
