![Corda](https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png)

# CordaPoc

Bem-vindo(a) ao CordaPoc. O CordaPoc é baseado no [tutorial CorDapp](http://docs.corda.net/tutorial-cordapp.html) encontrado no site de documentação do Corda.

**Esta é a versão em KOTLIN do CordaPoc.**

**Caso esteja interessado(a) em explorar o conteúdo em código do Corda,
contribuir ao core da plataforma Corda ou ver e executar os
demos, clone o [repositório corda](https://github.com/corda/corda).**

*Leia em outros idiomas: [English](README.md)*

## Pré-Requisitos

Será necessário instalar o seguinte antes de começar:

* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) 
  instalado e alcançável no seu path (Versão mínima: 1.8_131).
* [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) (Versão mínima: 2017.1)
* git
* Opcional: [h2 web console](http://www.h2database.com/html/download.html)
  (baixe o "platform-independent zip")

Para mais informações, veja a página
[getting set up](https://docs.corda.net/getting-set-up.html) no site de documentação do Corda.

## Preparação

Para começar, clone o repositório com o comando:

     git clone https://github.com/douglasmorais/cordaPoc.git

E mude o diretório para o recém clonado repositório:

     cd cordaPoc

## Gerando os Nós

**Unix:** 

     ./gradlew deployNodes

**Windows:**

     gradlew.bat deployNodes

Obs: Você precisará fazer esse passo após qualquer modificação que fizer no projeto,
para que tenha efeito aos nós.

## Rodando os Nós

Com os nós gerados, mude para a pasta em que eles estão:

     cd build/nodes

O script de geração de nós do Gradle terá criado uma pasta para cada nó. Existirão três pastas,
uma para cada nó e um script `runnodes`.

## Interagindo com os Nós

Na janela de terminal referente ao nó PartyA, o comando 'help' mostrará uma lista com os comandos disponíveis.

Primeiramente, queremos fazer um IPO, com ações da empresa "A". Iniciamos o `IPOFlow` com o seguinte comando:

     start IPOFlow shareValue: 100, owner: "O=PartyA,L=London,C=GB", codigoAcao: "A"

Se o fluxo funcionou, ele deverá estar salvo na vault do nó PartyA. Para visualizar:

     run vaultQuery contractStateType: com.template.ShareState

A vault do nó PartyA deverá mostrar o seguinte:

     states:
     - state:
         data:
           shareValue: 100
           owner: "C=GB,L=London,O=PartyA"
           codigoAcao: "A"
           participants:
           - "C=GB,L=London,O=PartyA"
         contract: "com.template.ShareContract"
         notary: "C=GB,L=London,O=Controller,CN=corda.notary.validating"
         encumbrance: null
         constraint:
           attachmentId: "4273B55BA46E3ACF06B1C11865526FC84F51ABF3CE489A2DCCB8149B8187B18F"
       ref:
         txhash: "E4F20D3FC7016B470110DC063001671BF0ECDDDE99C5337A9D31C55CB597072E"
         index: 0
     statesMetadata:
     - ref:
         txhash: "E4F20D3FC7016B470110DC063001671BF0ECDDDE99C5337A9D31C55CB597072E"
         index: 0
       contractStateClassName: "com.template.ShareState"
       recordedTime: 1521834554.082000000
       consumedTime: null
       status: "UNCONSUMED"
       notary: "C=GB,L=London,O=Controller,CN=corda.notary.validating"
       lockId: null
       lockUpdateTime: 1521834554.142000000
     totalStatesAvailable: -1
     stateTypes: "UNCONSUMED"
     otherResults: []

Em seguida, podemos mudar o valor da ação, com:

     start ChangeValueFlow shareValue: 80, owner: "O=PartyA,L=London,C=GB", codigoAcao: "A"

Assim, a vault do nó PartyA mostrará somente o último estado, com o novo valor:

     states:
     - state:
         data:
           shareValue: 80
           owner: "C=GB,L=London,O=PartyA"
           codigoAcao: "A"
           participants:
           - "C=GB,L=London,O=PartyA"
         contract: "com.template.ShareContract"
         notary: "C=GB,L=London,O=Controller,CN=corda.notary.validating"
         encumbrance: null
         constraint:
           attachmentId: "A1FED7C10FBB02F9F13EB6EBEB59730CAF1D73DD0AB680BAD1D8338BCA235232"
       ref:
         txhash: "A337543D7D59E7047F86898BF25D60E64A249A985ADF6A0497AA5D944F1A4236"
         index: 0
     statesMetadata:
     - ref:
         txhash: "A337543D7D59E7047F86898BF25D60E64A249A985ADF6A0497AA5D944F1A4236"
         index: 0
       contractStateClassName: "com.template.ShareState"
       recordedTime: 1522091941.076000000
       consumedTime: null
       status: "UNCONSUMED"
       notary: "C=GB,L=London,O=Controller,CN=corda.notary.validating"
       lockId: null
       lockUpdateTime: 1522091941.175000000
     totalStatesAvailable: -1
     stateTypes: "UNCONSUMED"
     otherResults: []

Finalmente, podemos mudar o dono da ação:

     start TradeFlow shareValue: 90, owner: "O=PartyB,L=New York,C=US", codigoAcao: "A"

A vault do nó PartyA deverá mostrar o seguinte:

     states: []
     statesMetadata: []
     totalStatesAvailable: -1
     stateTypes: "UNCONSUMED"
     otherResults: []

Na janela de terminal relativa ao nó PartyB digite o comando para visualizar sua vault:

     run vaultQuery contractStateType: com.template.ShareState

A vault do nó PartyB deverá mostrar o seguinte:

     states:
     - state:
         data:
           shareValue: 90
           owner: "C=US,L=New York,O=PartyB"
           codigoAcao: "A"
           participants:
           - "C=US,L=New York,O=PartyB"
         contract: "com.template.ShareContract"
         notary: "C=GB,L=London,O=Controller,CN=corda.notary.validating"
         encumbrance: null
         constraint:
           attachmentId: "A1FED7C10FBB02F9F13EB6EBEB59730CAF1D73DD0AB680BAD1D8338BCA235232"
       ref:
         txhash: "112557268D7827A6A37FB42A270F9492B93ABAEC8D676C4297407BA0952A3B84"
         index: 0
     statesMetadata:
     - ref:
         txhash: "112557268D7827A6A37FB42A270F9492B93ABAEC8D676C4297407BA0952A3B84"
         index: 0
       contractStateClassName: "com.template.ShareState"
       recordedTime: 1522092209.811000000
       consumedTime: null
       status: "UNCONSUMED"
       notary: "C=GB,L=London,O=Controller,CN=corda.notary.validating"
       lockId: null
       lockUpdateTime: 1522092210.088000000
     totalStatesAvailable: -1
     stateTypes: "UNCONSUMED"
     otherResults: []

## Interagindo com o CordaPoc via HTTP

O CordaPoc define endpoints da API e disponibilizam certo conteúdo
web estático. Inicialmente, eles retornam respostas genéricas.

Os nós podem ser encontrados usando as seguintes portas, definidas em
`build.gradle`, e no arquivo `node.conf` para cada nó presente
em `build/nodes/partyX`:

     PartyA: localhost:10007
     PartyB: localhost:10010

Assim que os nós iniciem, deverão ser mostradas em quais host e porta os
servidores web estão rodando. Os endpoints disponibilizados da API estão em:

     /api/template/templateGetEndpoint

O conteúdo web estático é disponibilizado por:

     /web/template

## Usando o Cliente de Examplo RPC

The `ExampleClient.kt` file is a simple utility which uses the client
RPC library to connect to a node and log its transaction activity.
It will log any existing states and listen for any future states. To build 
the client use the following Gradle task:

     ./gradlew runTemplateClient

To run the client:

**Via IntelliJ:**

Select the 'Run Template RPC Client'
run configuration which, by default, connect to PartyA (RPC port 10006). Click the
Green Arrow to run the client.

**Via the command line:**

Run the following Gradle task:

     ./gradlew runTemplateClient
     
Note that the template rPC client won't output anything to the console as no state 
objects are contained in either PartyA's or PartyB's vault.

## Running the Nodes Across Multiple Machines

The nodes can also be set up to communicate between separate machines on the 
same subnet.

After deploying the nodes, navigate to the build folder (`build/
nodes`) and move some of the individual node folders to 
separate machines on the same subnet (e.g. using a USB key). It is important 
that no nodes - including the controller node - end up on more than one 
machine. Each computer should also have a copy of `runnodes` and 
`runnodes.bat`.

For example, you may end up with the following layout:

* Machine 1: `controller`, `partya`, `runnodes`, `runnodes.bat`
* Machine 2: `partyb`, `partyc`, `runnodes`, `runnodes.bat`

You must now edit the configuration file for each node, including the 
controller. Open each node's config file (`[nodeName]/node.conf`), and make 
the following changes:

* Change the artemis address to the machine's ip address (e.g. 
  `artemisAddress="10.18.0.166:10005"`)
* Change the network map address to the ip address of the machine where the 
  controller node is running (e.g. `networkMapAddress="10.18.0.166:10002"`) 
  (please note that the controller will not have a network map address)

Each machine should now run its nodes using `runnodes` or `runnodes.bat` 
files. Once they are up and running, the nodes should be able to communicate 
among themselves in the same way as when they were running on the same machine.

## Conteúdo adicional

Tutoriais e documentação para desenvolvedores de CorDapps e Corda estão disponíveis
[aqui](https://docs.corda.net/).