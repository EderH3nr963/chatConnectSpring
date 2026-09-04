Aqui está o documento **`WEBSOCKET.md`** atualizado e sincronizado com o seu código do Spring Boot.

---

# Documentação WebSocket & STOMP

Esta API utiliza o protocolo **STOMP sobre WebSocket** para comunicação bi-direcional em tempo real.

## Conexão e Autenticação

* **Endpoint de Conexão:** `ws://localhost:8080/ws`
* **Protocolo:** STOMP sobre WebSocket (com suporte a SockJS)
* **Autenticação:** Realizada no comando `CONNECT` enviando as credenciais do Clerk nos headers nativos do STOMP.

### Exemplo de Conexão (Frontend)

```javascript
const stompClient = new StompJs.Client({
    brokerURL: 'ws://localhost:8080/ws',
    connectHeaders: {
        'Authorization': 'Bearer <CLERK_JWT_TOKEN>'
    }
});

```

---

## 1. Comandos do Cliente (Enviar Dados para o Servidor)

O cliente dispara comandos para o servidor utilizando o método `send()` do cliente STOMP.

### 1.1. Enviar Mensagem

* **Destino:** `/app/chat.sendMessage`
* **Descrição:** Envia uma nova mensagem para um chat.

**Payload:**

```json
{
  "chatId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "content": "Olá, tudo bem?"
}

```

### 1.2. Editar Mensagem

* **Destino:** `/app/chat.editMessage`
* **Descrição:** Edita o conteúdo de uma mensagem existente enviada pelo usuário.

**Payload:**

```json
{
  "messageId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "content": "Conteúdo editado da mensagem"
}

```

### 1.3. Deletar Mensagem

* **Destino:** `/app/chat.deleteMessage`
* **Descrição:** Remove uma mensagem.

**Payload:**

```json
{
  "messageId": "7c9e6679-7425-40de-944b-e07fc1f90ae7"
}

```

---

## 2. Inscrições / Subscrições (Escutar Dados do Servidor)

### 2.1. Tópico da Sala de Chat (`/topic/chat.{chatId}`)

Subscreva nesta rota **apenas quando o usuário estiver com a tela do chat aberta**.

* **Destino:** `/topic/chat.{chatId}` (Substitua `{chatId}` pelo UUID do chat)
* **Comportamento:** O servidor avisa essa sala sobre novas mensagens, edições e exclusões. Entrar nesta sala notifica o servidor sobre a presença ativa do usuário na tela.

---

### 2.2. Fila Individual de Notificações (`/user/queue/notifications`)

Subscreva nesta rota **assim que conectar no aplicativo** e mantenha a subscrição ativa enquanto o usuário estiver logado.

* **Destino:** `/user/queue/notifications`
* **Comportamento:** Recebe eventos de novas mensagens apenas para chats que o usuário **NÃO** está visualizando no momento (para atualizar o contador de não lidas e exibir avisos/badges).

---

## 3. Estrutura dos Eventos Recebidos (`MessageWebSocketEventDTO`)

Todos os eventos emitidos pelo servidor seguem o schema único `MessageWebSocketEventDTO`:

| Campo | Tipo | Descrição |
| --- | --- | --- |
| `eventType` | `String` | Tipo do evento: `"MESSAGE_SENT"`, `"MESSAGE_EDITED"`, `"MESSAGE_DELETED"` |
| `chatId` | `UUID` | ID do chat onde o evento ocorreu |
| `messageId` | `UUID` | ID da mensagem afetada |
| `message` | `Object` | Objeto `MessageResponseDTO` com os dados da mensagem (nulo no evento `MESSAGE_DELETED`) |
| `userId` | `UUID` | ID do usuário relacionado ao evento (opcional) |
| `userName` | `String` | Nome do usuário relacionado ao evento (opcional) |

---

### Exemplos de Payloads de Eventos

#### A. Evento: Nova Mensagem Criada (`MESSAGE_SENT`)

*(Recebido via `/topic/chat.{chatId}` ou `/user/queue/notifications`)*

```json
{
  "eventType": "MESSAGE_SENT",
  "chatId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "messageId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "message": {
    "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
    "chatId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "senderId": "11111111-2222-3333-4444-555555555555",
    "content": "Olá, tudo bem?",
    "createdAt": "2026-09-04T11:52:00Z"
  },
  "userId": null,
  "userName": null
}

```

#### B. Evento: Mensagem Editada (`MESSAGE_EDITED`)

*(Recebido via `/topic/chat.{chatId}`)*

```json
{
  "eventType": "MESSAGE_EDITED",
  "chatId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "messageId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "message": {
    "id": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
    "chatId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "senderId": "11111111-2222-3333-4444-555555555555",
    "content": "Conteúdo editado da mensagem",
    "createdAt": "2026-09-04T11:52:00Z"
  },
  "userId": null,
  "userName": null
}

```

#### C. Evento: Mensagem Deletada (`MESSAGE_DELETED`)

*(Recebido via `/topic/chat.{chatId}`)*

```json
{
  "eventType": "MESSAGE_DELETED",
  "chatId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "messageId": "7c9e6679-7425-40de-944b-e07fc1f90ae7",
  "message": null,
  "userId": null,
  "userName": null
}

```