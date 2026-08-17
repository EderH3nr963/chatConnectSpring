# Requisitos Funcionais

| RF | Requisito |
|---|---|
| **RF0001** | O sistema deve permitir a criação e exclusão de chats. |
| **RF0002** | Um chat deve possuir no mínimo 1 participante. |
| **RF0003** | O sistema não deve permitir a criação de um chat sem ao menos 1 participante. |
| **RF0004** | O sistema não deve permitir que um chat do tipo `CHAT` possua mais de 2 participantes. |
| **RF0005** | Apenas usuários que possuam a role `ADMIN` no chat podem remover participantes ou renomear o grupo. |
| **RF0006** | Caso o último administrador deixe o chat, o participante mais antigo deverá receber a role `ADMIN`. |
| **RF0007** | O sistema não deve permitir que um usuário envie mensagens para um chat do qual não participa. |
| **RF0008** | Apenas usuários que possuam a role `ADMIN` no chat podem excluir o grupo. |
| **RF0009** | Caso um chat fique sem participantes, o sistema deverá excluí-lo automaticamente. |
| **RF0010** | Antes da exclusão de um chat, o sistema deverá excluir todas as mensagens associadas a ele. |