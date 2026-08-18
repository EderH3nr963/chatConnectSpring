# Requisitos Funcionais

| RF         | Requisito                                                                                                             |
| ---------- | --------------------------------------------------------------------------------------------------------------------- |
| **RF0001** | O sistema deve permitir a criação e exclusão de chats.                                                                |
| **RF0002** | Um chat deve possuir no mínimo 1 participante.                                                                        |
| **RF0003** | O sistema não deve permitir a criação de um chat sem ao menos 1 participante.                                         |
| **RF0004** | O sistema não deve permitir que um chat do tipo `PRIVATE` possua mais de 2 participantes.                             |
| **RF0005** | Apenas usuários que possuam a role `ADMIN` no chat podem remover participantes ou alterar as informações de um grupo. |
| **RF0006** | Caso o último administrador deixe o chat, o participante mais antigo deverá receber a role `ADMIN`.                   |
| **RF0007** | O sistema não deve permitir que um usuário envie mensagens para um chat do qual não participa.                        |
| **RF0008** | Apenas usuários que possuam a role `ADMIN` no chat podem excluir um chat do tipo `GROUP`.                             |
| **RF0009** | Caso um chat fique sem participantes, o sistema deverá excluí-lo automaticamente.                                     |
| **RF0010** | Antes da exclusão de um chat, o sistema deverá excluir todas as mensagens associadas a ele.                           |
| **RF0011** | Caso o chat seja do tipo `PRIVATE`, o nome do chat deverá corresponder ao nome do outro participante.                 |
| **RF0012** | O sistema deve permitir a criação de chats do tipo `PRIVATE` entre dois usuários distintos.                           |
| **RF0013** | O sistema deve permitir a criação de chats do tipo `GROUP` com múltiplos participantes.                               |
| **RF0014** | O usuário responsável pela criação de um chat deverá receber a role `ADMIN`.                                          |
| **RF0015** | O sistema não deve permitir que um usuário seja adicionado mais de uma vez ao mesmo chat.                             |
| **RF0016** | O sistema deve permitir que usuários participantes de um chat deixem o chat voluntariamente.                          |
| **RF0017** | O sistema deve permitir que usuários com a role `ADMIN` adicionem participantes a um chat do tipo `GROUP`.            |
| **RF0018** | O sistema deve permitir que usuários com a role `ADMIN` removam participantes de um chat do tipo `GROUP`.             |
| **RF0019** | O sistema deve permitir que usuários com a role `ADMIN` alterem o título e a descrição de um chat do tipo `GROUP`.    |
| **RF0020** | O sistema deve permitir a consulta de um chat pelo seu identificador.                                                 |
| **RF0021** | O sistema deve permitir a consulta dos chats dos quais determinado usuário participa.                                 |
| **RF0022** | O sistema não deve permitir que um usuário consulte informações de um chat do qual não participa.                     |
| **RF0023** | O sistema deve validar a existência dos usuários antes de adicioná-los como participantes de um chat.                 |
| **RF0024** | O sistema deve garantir que um chat do tipo `PRIVATE` possua exatamente dois participantes.                           |
| **RF0025** | O sistema deve permitir a identificação da role de cada participante dentro do chat.                                  |

