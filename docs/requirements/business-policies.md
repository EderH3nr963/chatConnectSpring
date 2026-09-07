# Regras de Negócio (RN) - ChatConnect

Este documento descreve as regras de negócio que regem o comportamento do sistema **ChatConnect**, garantindo a integridade dos dados, a segurança e a consistência das operações de chat e mensageria.

---

## 1. Tabela Resumida de Regras de Negócio

| ID | Regra | Descrição |
|---|---|---|
| **RN0001** | Quantidade mínima de participantes | Um chat deve possuir no mínimo 1 participante para existir. |
| **RN0002** | Composição do chat privado | Um chat do tipo `PRIVATE` deve possuir exatamente 2 participantes distintos. |
| **RN0003** | Limite máximo do chat privado | Um chat do tipo `PRIVATE` não pode possuir mais de 2 participantes em nenhuma hipótese. |
| **RN0004** | Permissão para remoção em grupos | Apenas participantes com a role `ADMIN` possuem permissão para remover participantes de um chat do tipo `GROUP`. |
| **RN0005** | Permissão para edição de grupos | Apenas participantes com a role `ADMIN` podem alterar o título e a descrição de um chat do tipo `GROUP`. |
| **RN0006** | Sucessão automática de liderança | Caso o último administrador deixe o chat em grupo, o participante mais antigo restante deverá receber automaticamente a role `ADMIN`. |
| **RN0007** | Bloqueio de envio por não participantes | Um usuário não pode enviar mensagens para um chat do qual não faça parte como participante ativo. |
| **RN0008** | Permissão para exclusão de grupos | Apenas participantes com a role `ADMIN` podem excluir um chat do tipo `GROUP`. |
| **RN0009** | Exclusão automática de chat vazio | Caso um chat fique sem nenhum participante, ele deverá ser excluído automaticamente pelo sistema. |
| **RN0010** | Exclusão de mensagens em cascata | Antes da exclusão de um chat, todas as mensagens e vínculos de participantes associados a ele deverão ser excluídos de forma transacional. |
| **RN0011** | Título dinâmico de chat privado | Em um chat do tipo `PRIVATE`, o título exibido para o usuário deve corresponder dinamicamente ao nome (`username`) do outro participante da conversa. |
| **RN0012** | Atribuição inicial de ADMIN | O usuário responsável pela criação de um chat (seja `PRIVATE` ou `GROUP`) recebe automaticamente a role `ADMIN`. |
| **RN0013** | Unicidade de participante por chat | Um usuário não pode ser adicionado mais de uma vez ao mesmo chat (relação 1:1 entre usuário e chat nos participantes). |
| **RN0014** | Validação prévia de existência de usuário | Os usuários devem existir previamente cadastrados no sistema para que possam ser adicionados como participantes de qualquer chat. |
| **RN0015** | Permissão de envio de mensagens | Apenas usuários formalmente cadastrados como participantes do chat podem enviar mensagens para o mesmo. |
| **RN0016** | Permissão para exclusão de mensagens | Apenas o próprio autor que enviou a mensagem ou um participante com a role `ADMIN` do chat podem excluir mensagens. |
| **RN0017** | Permissão exclusiva para edição de mensagens | Apenas o participante que enviou a mensagem (autor) pode editá-la. Nem mesmo administradores podem alterar o conteúdo de mensagens de outros usuários. |
| **RN0018** | Proibição de auto-chat privado | Um usuário não pode criar um chat do tipo `PRIVATE` consigo mesmo. O destinatário deve ser obrigatoriamente um usuário distinto. |
| **RN0019** | Imutabilidade da composição de chat privado | Não é permitido adicionar novos participantes nem remover participantes existentes em um chat do tipo `PRIVATE`. |
| **RN0020** | Proibição de auto-remoção administrativa | Um participante com role `ADMIN` não pode remover a si próprio através do endpoint administrativo de remoção (deve utilizar a saída voluntária do chat). |
| **RN0021** | Proteção contra remoção entre administradores | Um participante com role `ADMIN` não pode remover outro participante que também possua a role `ADMIN` do mesmo grupo. |
| **RN0022** | Validação de conteúdo da mensagem | O conteúdo da mensagem não pode ser nulo, vazio ou composto exclusivamente por espaços em branco. O conteúdo deve ser sanitizado (`trim`). |
| **RN0023** | Zeramento de mensagens não lidas por presença | Ao entrar/abrir a sala de chat (subscrição ativa no canal WebSocket `/topic/chat.{chatId}`), a contagem de mensagens não lidas (`unreadMessages`) do participante é zerada e a presença ativa é registrada no Redis. |
| **RN0024** | Incremento seletivo de não lidas | Ao enviar uma mensagem, o contador `unreadMessages` é incrementado apenas para os participantes do chat que **não** estiverem com a tela do chat aberta no momento (inativos na sala). |
| **RN0025** | Sincronização via Webhooks do Provedor de Identidade | Usuários do sistema são criados, atualizados e removidos automaticamente através de eventos seguros de webhook (`user.created`, `user.updated`, `user.deleted`) emitidos pelo Clerk. |
| **RN0026** | Autenticação obrigatória e stateless | Todas as operações de chat e mensageria (HTTP e WebSocket) requerem um token JWT válido emitido pelo Clerk e correspondência do usuário na base de dados. |

---

## 2. Detalhamento por Módulo

### 2.1. Módulo de Chats
- **Tipos de Chat Suportados**: `PRIVATE` (conversa direta entre dois usuários) e `GROUP` (sala com múltiplos participantes).
- **Criação**: Ao criar um grupo, o criador define título e descrição e já pode incluir múltiplos participantes (todos recebem role `DEFAULT`, e o criador `ADMIN`).
- **Nomes em Chats Privados**: Chats privados não armazenam título estático. O título é resolvido dinamicamente com base no `username` do interlocutor.
- **Exclusão**: Exclusão de chats só pode ser feita por administradores e deve limpar todas as mensagens e participantes em uma única transação atômica.

### 2.2. Módulo de Participantes e Permissões (RBAC)
- **Roles**:
  - `ADMIN`: Criador do chat ou participante promovido. Pode alterar dados do grupo, adicionar novos membros, remover participantes com role `DEFAULT` e excluir qualquer mensagem.
  - `DEFAULT`: Participante comum. Pode ler, enviar, editar suas próprias mensagens e excluir suas próprias mensagens, além de sair do grupo voluntariamente.
- **Regras de Exclusão de Membros**:
  - Um admin não pode remover outro admin.
  - Um admin não pode remover a si próprio pelo endpoint de remoção.
  - Apenas administradores podem adicionar participantes a grupos.

### 2.3. Módulo de Mensagens e Presença
- **Integridade de Mensagens**: Mensagens enviadas devem conter texto válido. Espaços no início e fim são removidos automaticamente.
- **Edição Restrita**: O autor original é a única entidade autorizada a editar o texto de sua mensagem.
- **Exclusão Mista**: Mensagens podem ser deletadas pelo autor ou por qualquer administrador daquele chat.
- **Presença e Leitura em Tempo Real**:
  - Usuários que subscrevem em `/topic/chat.{chatId}` são marcados como ativos no Redis.
  - Ao sair ou desinscrever, a presença é removida.
  - Participantes inativos no momento do envio recebem notificação pessoal em `/user/queue/notifications` com o evento `MESSAGE_SENT` e têm o contador `unreadMessages` incrementado no banco relacional.

### 2.4. Módulo de Usuários e Autenticação
- **Identidade Centralizada**: Gerenciamento de credenciais, senhas e fluxos de autenticação é 100% terceirizado para o Clerk.
- **Sincronismo de Dados**: O sistema local mantém uma tabela `"user"` sincronizada contendo `id` (UUID), `clerk_user_id`, `email`, `username`, `created_at` e `updated_at`.