![Corda](https://www.corda.net/wp-content/uploads/2016/11/fg005_corda_b.png)

# CordaPoc

Welcome to the CordaPoc. The CordaPoc is based on the [CorDapp tutorial](http://docs.corda.net/tutorial-cordapp.html) found on the Corda docsite.

**This is the KOTLIN version of the CordaPoc.**

**Instead, if you are interested in exploring the Corda codebase itself,
contributing to the core Corda platform or viewing and running sample
demos then clone the [corda repository](https://github.com/corda/corda).**

## Pre-Requisites

You will need the following installed on your machine before you can start:

* [JDK 8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) 
  installed and available on your path (Minimum version: 1.8_131).
* [IntelliJ IDEA](https://www.jetbrains.com/idea/download/) (Minimum version 2017.1)
* git
* Optional: [h2 web console](http://www.h2database.com/html/download.html)
  (download the "platform-independent zip")

For more detailed information, see the
[getting set up](https://docs.corda.net/getting-set-up.html) page on the
Corda docsite.

## Getting Set Up

To get started, clone this repository with:

     git clone https://github.com/douglasmorais/cordaPoc.git

And change directories to the newly cloned repo:

     cd cordaPoc

## Building the CorDapp template:

**Unix:** 

     ./gradlew deployNodes

**Windows:**

     gradlew.bat deployNodes

Note: You'll need to re-run this build step after making any changes to
the template for these to take effect on the node.

## Running the Nodes

Once the build finishes, change directories to the folder where the newly
built nodes are located:

     cd build/nodes

The Gradle build script will have created a folder for each node. You'll
see three folders, one for each node and a `runnodes` script.

## Interacting with the nodes

Go to the terminal window displaying the CRaSH shell of PartyA. Typing 'help' will display a list of the available commands.

We want to create an IPO, with shares from company "A". We start the 'IPOFlow' by typing:

     start IPOFlow shareValue: 100, owner: "O=PartyA,L=London,C=GB", codigoAcao: "A"

If the flow worked, it should have recorded in the vault of PartyA. Let's check, running:

     run vaultQuery contractStateType: com.template.ShareState

The vault of PartyA should display the following output:

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

## Interacting with the CorDapp via HTTP

The CorDapp defines a couple of HTTP API end-points and also serves some
static web content. Initially, these return generic template responses.

The nodes can be found using the following port numbers, defined in 
`build.gradle`, as well as the `node.conf` file for each node found
under `build/nodes/partyX`:

     PartyA: localhost:10007
     PartyB: localhost:10010

As the nodes start up, they should tell you which host and port their
embedded web server is running on. The API endpoints served are:

     /api/template/templateGetEndpoint

And the static web content is served from:

     /web/template

## Using the Example RPC Client

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

## Further reading

Tutorials and developer docs for CorDapps and Corda are
[here](https://docs.corda.net/).