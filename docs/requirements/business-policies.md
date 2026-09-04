# Regras de Negócios

| RN         | Descrição                                                                                              |
|------------|--------------------------------------------------------------------------------------------------------|
| **RN0001** | Um chat deve possuir no mínimo 1 participante.                                                         |
| **RN0002** | Um chat do tipo `PRIVATE` deve possuir exatamente 2 participantes.                                     |
| **RN0003** | Um chat do tipo `PRIVATE` não pode possuir mais de 2 participantes.                                    |
| **RN0004** | Apenas participantes com a role `ADMIN` podem remover participantes de um chat do tipo `GROUP`.        |
| **RN0005** | Apenas participantes com a role `ADMIN` podem alterar as informações de um chat do tipo `GROUP`.       |
| **RN0006** | Caso o último administrador deixe o chat, o participante mais antigo deverá receber a role `ADMIN`.    |
| **RN0007** | Um usuário não pode enviar mensagens para um chat do qual não participa.                               |
| **RN0008** | Apenas participantes com a role `ADMIN` podem excluir um chat do tipo `GROUP`.                         |
| **RN0009** | Caso um chat fique sem participantes, ele deverá ser excluído automaticamente.                         |
| **RN0010** | Antes da exclusão de um chat, todas as mensagens associadas a ele deverão ser excluídas.               |
| **RN0011** | Em um chat do tipo `PRIVATE`, o nome do chat deverá corresponder ao nome do outro participante.        |
| **RN0012** | O usuário responsável pela criação de um chat deverá receber a role `ADMIN`.                           |
| **RN0013** | Um usuário não pode ser adicionado mais de uma vez ao mesmo chat.                                      |
| **RN0014** | Os usuários devem existir no sistema para que possam ser adicionados como participantes de um chat.    |
| **RN0015** | Apenas participantes do chat podem enviar mensagens                                                    |
| **RN0016** | Apenas o participante que enviou a mensagem ou participante com a role `ADMIN` podem excluir mensagens |
| **RN0017** | Apenas o participante que enviou a mensagem pode editá-la                                              |