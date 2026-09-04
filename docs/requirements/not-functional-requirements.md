# Requisitos Não Funcionais

| RNF         | Requisito                                                                                                                                                            |
|-------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **RNF0001** | O sistema deve utilizar autenticação e autorização para garantir que somente usuários autenticados possam acessar os recursos de chat.                               |
| **RNF0002** | O sistema deve garantir que as operações sobre chats e participantes sejam executadas de forma transacional, evitando estados inconsistentes.                        |
| **RNF0003** | O sistema deve garantir a consistência dos participantes de um chat durante operações concorrentes.                                                                  |
| **RNF0004** | O sistema deve utilizar identificadores únicos para chats e participantes.                                                                                           |
| **RNF0005** | O sistema deve garantir a integridade referencial entre chats, participantes e mensagens.                                                                            |
| **RNF0006** | O sistema deve impedir o acesso a informações de chats por usuários que não possuam permissão para acessá-las.                                                       |
| **RNF0007** | As operações de criação, alteração e exclusão de chats devem possuir tratamento adequado de erros, retornando informações apropriadas ao cliente.                    |
| **RNF0008** | O sistema deve registrar erros ocorridos durante operações relacionadas a chats para facilitar diagnóstico e manutenção.                                             |
| **RNF0009** | A arquitetura do módulo de chat deve manter separadas as regras de negócio, os casos de uso e os mecanismos de infraestrutura.                                       |
| **RNF0010** | As regras de negócio do módulo de chat não devem depender diretamente de frameworks ou tecnologias de persistência.                                                  |
| **RNF0011** | O sistema deve suportar comunicação em tempo real para o envio e recebimento de novas mensagens entre os participantes conectados.                                   |
| **RNF0012** | O sistema deve garantir que mensagens destinadas a um chat sejam encaminhadas somente aos participantes autorizados daquele chat.                                    |
| **RNF0013** | As operações de consulta de chats devem possuir tempo de resposta adequado para utilização em aplicações de comunicação em tempo real.                               |
| **RNF0014** | O sistema deve ser capaz de suportar múltiplos usuários conectados simultaneamente sem comprometer a consistência dos dados.                                         |
| **RNF0015** | O sistema deve utilizar persistência capaz de manter os dados dos chats após reinicializações da aplicação.                                                          |
| **RNF0016** | O sistema deve utilizar controle transacional para garantir que a criação de um chat e seus participantes seja concluída integralmente ou desfeita em caso de falha. |
| **RNF0017** | O sistema deve permitir a evolução dos mecanismos de comunicação em tempo real sem exigir alterações nas regras de negócio do domínio.                               |
| **RNF0018** | O sistema deve manter as credenciais e informações de autenticação protegidas, não expondo dados sensíveis nas respostas da API.                                     |
| **RNF0019** | O sistema deve armazenar a senha criptografada no banco de de dados                                                                                                  |
| **RNF0020** | O sistem deve armazenar a ultima data de login e atualização do usuário                                                                                              |
