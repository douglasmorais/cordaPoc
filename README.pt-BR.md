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

Nós iniciados via terminal mostrarão um shell interativo:

    Welcome to the Corda interactive shell.
    Useful commands include 'help' to see what is available, and 'bye' to shut down the node.

    Thu Apr 19 09:18:09 BRT 2018>>>

Na janela de terminal referente ao nó PartyA, o comando `help` mostrará uma lista com os comandos disponíveis.

O comando `flow list` mostra uma lista com os flows que o nó pode rodar. Em nosso caso, irá retornar a seguinte lista:

     com.template.ChangeValueFlow
     com.template.IPOFlow
     com.template.TradeFlow
     net.corda.core.flows.ContractUpgradeFlow$Authorise
     net.corda.core.flows.ContractUpgradeFlow$Deauthorise
     net.corda.core.flows.ContractUpgradeFlow$Initiate
     net.corda.finance.flows.CashConfigDataFlow
     net.corda.finance.flows.CashExitFlow
     net.corda.finance.flows.CashIssueAndPaymentFlow
     net.corda.finance.flows.CashIssueFlow
     net.corda.finance.flows.CashPaymentFlow

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

O CordaPoc define endpoints da API e disponibiliza conteúdo
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

## Rodando os nós em diferentes máquinas

Os nós podem ser configurados para se comunicarem por diferentes máquinas na mesma sub-rede.

Após gerar os nós, navegue até o diretório em que eles foram gerados (`build/nodes`) e mova algumas das pastas dos nós para
máquinas separadas na mesma sub-rede (ex: usando um pendrive). É importante que nenhum nó ('controller' inclusive) esteja
em mais de uma máquina. Cada computador deve ter uma cópia de `runnodes` e `runnodes.bat`.

Por exemplo, uma possível disposição:

* Máquina 1: `controller`, `partya`, `runnodes`, `runnodes.bat`
* Máquina 2: `partyb`, `partyc`, `runnodes`, `runnodes.bat`

É necessário ainda editar o arquivo de configuração de cada nó, controller inclusive.
Abra o arquivo de configuração de cada um dos nós (`[nodeName]/node.conf`), e faça as seguintes mudanças:

* Mude o artemisAddress para o endereço ip da máquina (ex: `artemisAddress="10.18.0.166:10005"`)
* Mude o networkMapAddress para o endereço ip da máquina em que o nó controller está rodando (ex: `networkMapAddress="10.18.0.166:10002"`)
  (observe que o controller não terá um networkMapAddress)

Cada computador deve rodar seus nós usando os arquivos `runnodes` ou `runnodes.bat`.
Assim que os nós estiverem rodando, deverão conseguir se comunicar entre si do mesmo modo como se estivessem na mesma máquina.

## Conteúdo adicional

Tutoriais e documentação para desenvolvedores de CorDapps e Corda estão disponíveis
[aqui](https://docs.corda.net/).