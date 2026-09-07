# Requisitos Funcionais (RF) - ChatConnect

Este documento especifica os Requisitos Funcionais da plataforma **ChatConnect**, descrevendo as funcionalidades oferecidas pela API para clientes web, mobile e sistemas integrados.

---

## 1. Tabela Resumida de Requisitos Funcionais

| RF | Título | Descrição |
|---|---|---|
| **RF0001** | Criação de Chat Privado | O sistema deve permitir a criação de chats do tipo `PRIVATE` entre exatamente dois usuários distintos e cadastrados. |
| **RF0002** | Criação de Chat em Grupo | O sistema deve permitir a criação de chats do tipo `GROUP` contendo título, descrição opcional e uma lista inicial de participantes. |
| **RF0003** | Saída Voluntária de Chat | O sistema deve permitir que um participante ativo deixe voluntariamente um chat do qual participa. |
| **RF0004** | Adicionar Participantes ao Grupo | O sistema deve permitir que administradores de um grupo adicionem novos participantes ao chat. |
| **RF0005** | Remover Participantes do Grupo | O sistema deve permitir que administradores de um grupo removam participantes que possuam role comum (`DEFAULT`). |
| **RF0006** | Alterar Dados do Grupo | O sistema deve permitir que administradores alterem o título e a descrição de um chat do tipo `GROUP`. |
| **RF0007** | Consultar Chat por ID | O sistema deve permitir a consulta detalhada de um chat através de seu identificador UUID, validando se o solicitante é participante do mesmo. |
| **RF0008** | Listar Chats do Usuário | O sistema deve permitir a consulta de todos os chats nos quais o usuário autenticado participa, calculando dinamicamente o título para chats privados. |
| **RF0009** | Identificar Role de Participantes | O sistema deve retornar a role (`ADMIN` ou `DEFAULT`) de cada participante dentro do chat consultado. |
| **RF0010** | Autenticação Federada via Token | O sistema deve autenticar requisições HTTP e sessões WebSocket através da validação de tokens JWT emitidos pelo Clerk (Identity Provider). |
| **RF0011** | Sincronização de Criação de Usuários | O sistema deve processar o webhook `user.created` do Clerk para registrar novos usuários localmente na base do sistema. |
| **RF0012** | Sincronização de Atualização de Usuários | O sistema deve processar o webhook `user.updated` do Clerk para sincronizar alterações cadastrais (e-mail, nome de usuário) na base local. |
| **RF0013** | Sincronização de Exclusão de Usuários | O sistema deve processar o webhook `user.deleted` do Clerk para remover o registro do usuário na base local. |
| **RF0014** | Envio de Mensagens | O sistema deve permitir que participantes autorizados enviem mensagens para um chat tanto via API REST (`POST`) quanto via canal WebSocket STOMP (`/app/chat.sendMessage`). |
| **RF0015** | Edição de Mensagens | O sistema deve permitir que o autor de uma mensagem edite seu conteúdo textual via REST (`PATCH`) ou via WebSocket STOMP (`/app/chat.editMessage`). |
| **RF0016** | Exclusão de Mensagens | O sistema deve permitir que o autor da mensagem ou administradores do chat excluam uma mensagem via REST (`DELETE`) ou via WebSocket STOMP (`/app/chat.deleteMessage`). |
| **RF0017** | Consulta do Histórico de Mensagens | O sistema deve disponibilizar endpoint para listagem e recuperação do histórico de mensagens de um chat para participantes autorizados. |
| **RF0018** | Exclusão de Chat em Grupo | O sistema deve permitir que administradores de um grupo excluam o chat completamente, incluindo histórico de mensagens e participantes. |
| **RF0019** | Notificação em Tempo Real no Chat | O sistema deve publicar eventos de mensagens novas (`MESSAGE_SENT`), mensagens editadas (`MESSAGE_EDITED`) e mensagens excluídas (`MESSAGE_DELETED`) no tópico WebSocket da sala (`/topic/chat.{chatId}`). |
| **RF0020** | Notificação Individual de Mensagens Pendentes | O sistema deve encaminhar evento de nova mensagem para a fila privada (`/user/queue/notifications`) dos participantes que não estiverem com o chat aberto no momento. |
| **RF0021** | Notificação de Entrada e Saída de Grupos | O sistema deve notificar o usuário através de sua fila pessoal (`/user/queue/notifications`) quando ele for adicionado (`JOINED_CHAT`) ou removido (`LEAVED_CHAT`) de um chat. |
| **RF0022** | Gerenciamento de Presença Ativa na Sala | O sistema deve registrar e remover o estado de presença ativa do usuário na sala de chat (Redis) a partir da subscrição e desinscrição no canal WebSocket `/topic/chat.{chatId}`. |
| **RF0023** | Controle e Zeramento de Mensagens Não Lidas | O sistema deve manter o número de mensagens não lidas (`unreadMessages`) por participante, incrementando para inativos no momento do envio e zerando automaticamente quando o participante abre/entra na sala. |

---

## 2. Detalhamento dos Módulos Funcionais

### 2.1. Módulo de Usuários e Identidade
- **RF0010 - Autenticação Stateless**: Todo acesso aos endpoints privados exige o envio do header `Authorization: Bearer <clerk_token>`.
- **RF0011 a RF0013 - Webhooks do Clerk**: Endpoints públicos em `/api/webhooks/clerk` que recebem payloads do Clerk para sincronizar entidades de usuário (`id`, `clerk_user_id`, `email`, `username`).

### 2.2. Módulo de Chats
- **RF0001 e RF0002 - Criação de Chats**:
  - `POST /api/chat/private`: Recebe lista de participantes com exatamente 1 outro usuário.
  - `POST /api/chat/group`: Recebe título, descrição e lista inicial de UUIDs de participantes.
- **RF0007 e RF0008 - Consulta**:
  - `GET /api/chat`: Retorna todos os chats do usuário autenticado com status e participantes.
  - `GET /api/chat/{chatId}`: Retorna dados específicos do chat com verificação de autorização de participante.
- **RF0006 e RF0018 - Manutenção de Grupo**:
  - `PATCH /api/chat/{chatId}`: Atualização de título e descrição (restrito a `ADMIN`).
  - `DELETE /api/chat/{chatId}`: Exclusão total do grupo (restrito a `ADMIN`).

### 2.3. Módulo de Participantes
- **RF0004 e RF0005 - Gestão de Membros**:
  - `POST /api/chat/{chatId}/participants`: Adição em lote de novos participantes em grupos (`ADMIN`).
  - `DELETE /api/chat/{chatId}/participants/{participantId}`: Remoção de membro `DEFAULT` (`ADMIN`).
- **RF0003 - Saída Voluntária**:
  - Remoção do próprio registro de participante, acionando passagem de liderança caso necessário.

### 2.4. Módulo de Mensagens (REST + STOMP)
- **Canais Híbridos**: Todas as ações de mensagem podem ser disparadas via REST ou via STOMP:
  - Enviar: `POST /api/chat/{chatId}/messages` ou STOMP `/app/chat.sendMessage`
  - Editar: `PATCH /api/chat/{chatId}/messages/{messageId}` ou STOMP `/app/chat.editMessage`
  - Deletar: `DELETE /api/chat/{chatId}/messages/{messageId}` ou STOMP `/app/chat.deleteMessage`
  - Histórico: `GET /api/chat/{chatId}/messages`

### 2.5. Módulo de Tempo Real e Notificações (WebSocket + Redis)
- **Tópico da Sala (`/topic/chat.{chatId}`)**: Broadcast de mensagens para todos os clientes conectados e visualizando o chat.
- **Fila Privada (`/user/queue/notifications`)**: Entrega direcionada de badges, avisos de mensagens não lidas e eventos de entrada/saída de grupos.
- **Gerenciamento de Presença**: Integração nativa com Redis para controle de sessão e contadores de mensagens não lidas.
