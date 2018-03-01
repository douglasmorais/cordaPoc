package com.template

import co.paralleluniverse.fibers.Suspendable
import net.corda.core.contracts.Command
import net.corda.core.contracts.requireThat
import net.corda.core.contracts.StateAndContract
import net.corda.core.flows.*
import net.corda.core.identity.Party
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.serialization.SerializationWhitelist
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.webserver.services.WebServerPluginRegistry
import java.util.function.Function
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

// *****************
// * API Endpoints *
// *****************
@Path("template")
class TemplateApi(val rpcOps: CordaRPCOps) {
    // Accessible at /api/template/templateGetEndpoint.
    @GET
    @Path("templateGetEndpoint")
    @Produces(MediaType.APPLICATION_JSON)
    fun templateGetEndpoint(): Response {
        return Response.ok("Template GET endpoint.").build()
    }
}

// *********
// * Flows *
// *********
@InitiatingFlow
@StartableByRPC
class IPOFlow(val shareValue: Int, val enterprise: Party) : FlowLogic<Unit>() {
    object INICIO_IPO : ProgressTracker.Step("Inicio do Flow de IPO")
    object MONTANDO_IPO : ProgressTracker.Step("Montando a transacao de IPO")
    object VERIFICANDO_IPO : ProgressTracker.Step("Verificando a validade da transacao de IPO")
    object ASSINANDO_IPO : ProgressTracker.Step("Assinando a transacao de IPO")
    object GRAVANDO_IPO : ProgressTracker.Step("Enviando a transacao para o Notary e gravando localmente")

    override val progressTracker = ProgressTracker(INICIO_IPO, MONTANDO_IPO, VERIFICANDO_IPO,
            ASSINANDO_IPO, GRAVANDO_IPO)

    @Suspendable
    override fun call() {
        progressTracker.currentStep = INICIO_IPO
        val notary = serviceHub.networkMapCache.notaryIdentities[0]
        val me = serviceHub.myInfo.legalIdentities

        progressTracker.currentStep = MONTANDO_IPO
        val txBuilder = TransactionBuilder(notary = notary)

        val outputState = ShareState(shareValue, ourIdentity, enterprise)
        val outputContractAndState = StateAndContract(outputState, SHARE_CONTRACT_ID)
        val cmd = Command(ShareContract.Commands.IPO(), listOf(ourIdentity.owningKey, enterprise.owningKey))

        txBuilder.withItems(outputContractAndState, cmd)

        progressTracker.currentStep = VERIFICANDO_IPO
        txBuilder.verify(serviceHub)

        progressTracker.currentStep = ASSINANDO_IPO
        val signedTx = serviceHub.signInitialTransaction(txBuilder)

        val otherpartySession = initiateFlow(enterprise)

        val fullySignedTx = subFlow(CollectSignaturesFlow(signedTx, listOf(otherpartySession), CollectSignaturesFlow.tracker()))

        progressTracker.currentStep = GRAVANDO_IPO
        subFlow(FinalityFlow(fullySignedTx))
    }
}

@InitiatingFlow
@StartableByRPC
class ChangeFlow(val shareValue: Int, val enterprise: Party) : FlowLogic<Unit>() {
    object INICIO_CHANGE : ProgressTracker.Step("Inicio do Flow de variacao de preco")
    object MONTANDO_CHANGE : ProgressTracker.Step("Montando a transacao de variacao de preco")
    object VERIFICANDO_CHANGE : ProgressTracker.Step("Verificando a validade da transacao de variacao de preco")
    object ASSINANDO_CHANGE : ProgressTracker.Step("Assinando a transacao de variacao de preco")
    object GRAVANDO_CHANGE : ProgressTracker.Step("Enviando a transacao para o Notary e gravando localmente")

    override val progressTracker = ProgressTracker(INICIO_CHANGE, MONTANDO_CHANGE, VERIFICANDO_CHANGE,
            ASSINANDO_CHANGE, GRAVANDO_CHANGE)

    @Suspendable
    override fun call() {
        progressTracker.currentStep = INICIO_CHANGE
        val notary = serviceHub.networkMapCache.notaryIdentities[0]

        progressTracker.currentStep = MONTANDO_CHANGE
        val txBuilder = TransactionBuilder(notary = notary)

        val outputState = ShareState(shareValue, ourIdentity, enterprise)
        val outputContractAndState = StateAndContract(outputState, SHARE_CONTRACT_ID)
        val cmd = Command(ShareContract.Commands.Change(), listOf(ourIdentity.owningKey, enterprise.owningKey))

        txBuilder.withItems(outputContractAndState, cmd)

        progressTracker.currentStep = VERIFICANDO_CHANGE
        txBuilder.verify(serviceHub)

        progressTracker.currentStep = ASSINANDO_CHANGE
        val signedTx = serviceHub.signInitialTransaction(txBuilder)

        val otherpartySession = initiateFlow(enterprise)

        val fullySignedTx = subFlow(CollectSignaturesFlow(signedTx, listOf(otherpartySession), CollectSignaturesFlow.tracker()))

        progressTracker.currentStep = GRAVANDO_CHANGE
        subFlow(FinalityFlow(fullySignedTx))
    }
}

@InitiatingFlow
@StartableByRPC
class TradeFlow(val shareValue: Int, val otherParty: Party) : FlowLogic<Unit>() {
    object INICIO_TRADE : ProgressTracker.Step("Inicio do Flow de compra/venda")
    object MONTANDO_TRADE : ProgressTracker.Step("Montando a transacao de compra/venda")
    object VERIFICANDO_TRADE : ProgressTracker.Step("Verificando a validade da transacao de compra/venda")
    object ASSINANDO_TRADE : ProgressTracker.Step("Assinando a transacao de compra/venda")
    object GRAVANDO_TRADE : ProgressTracker.Step("Enviando a transacao para o Notary e gravando localmente")

    /** The progress tracker provides checkpoints indicating the progress of the flow to observers. */
    override val progressTracker = ProgressTracker(INICIO_TRADE, MONTANDO_TRADE, VERIFICANDO_TRADE, ASSINANDO_TRADE, GRAVANDO_TRADE)

    /** The flow logic is encapsulated within the call() method. */
    @Suspendable
    override fun call() {
        // We retrieve the notary identity from the network map
        progressTracker.currentStep = INICIO_TRADE
        val notary = serviceHub.networkMapCache.notaryIdentities[0]

        // We create a transaction builder
        progressTracker.currentStep = MONTANDO_TRADE
        val txBuilder = TransactionBuilder(notary = notary)

        // We create the transaction components
        val outputState = ShareState(shareValue, ourIdentity, otherParty)
        val outputContractAndState = StateAndContract(outputState, SHARE_CONTRACT_ID)
        val cmd = Command(ShareContract.Commands.Trade(), listOf(ourIdentity.owningKey, otherParty.owningKey))

        // We add the items to the builder
        txBuilder.withItems(outputContractAndState, cmd)

        // Verifying the transaction
        progressTracker.currentStep = VERIFICANDO_TRADE
        txBuilder.verify(serviceHub)

        // Signing the transaction
        progressTracker.currentStep = ASSINANDO_TRADE
        val signedTx = serviceHub.signInitialTransaction(txBuilder)

        // Creating a session with the other party
        val otherpartySession = initiateFlow(otherParty)

        // Obtaining the counterparty's signature
        val fullySignedTx = subFlow(CollectSignaturesFlow(signedTx, listOf(otherpartySession), CollectSignaturesFlow.tracker()))

        // Finalising the transaction
        progressTracker.currentStep = GRAVANDO_TRADE
        subFlow(FinalityFlow(fullySignedTx))
    }
}

@InitiatedBy(TradeFlow::class)
class TradeFlowResponder(val otherPartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val signTransactionFlow = object : SignTransactionFlow(otherPartySession, SignTransactionFlow.tracker()) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
                "This must be a share transaction." using (output is ShareState)
                val share = output as ShareState
                "The share's value can't be negative." using (share.shareValue > 0)
            }
        }

        subFlow(signTransactionFlow)
    }
}

@InitiatedBy(IPOFlow::class)
class IPOFlowResponder(val otherPartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val signTransactionFlow = object : SignTransactionFlow(otherPartySession, SignTransactionFlow.tracker()) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
                "This must be a share transaction." using (output is ShareState)
                val share = output as ShareState
                "The share's value can't be negative." using (share.shareValue > 0)
            }
        }

        subFlow(signTransactionFlow)
    }
}

@InitiatedBy(ChangeFlow::class)
class ChangeFlowResponder(val otherPartySession: FlowSession) : FlowLogic<Unit>() {
    @Suspendable
    override fun call() {
        val signTransationFlow = object : SignTransactionFlow(otherPartySession, SignTransactionFlow.tracker()) {
            override fun checkTransaction(stx: SignedTransaction) = requireThat {
                val output = stx.tx.outputs.single().data
                "This must be a share transaction." using (output is ShareState)
                val share = output as ShareState
                "The share's value can't be negative." using (share.shareValue > 0)
            }
        }

        subFlow(signTransationFlow)
    }
}


// ***********
// * Plugins *
// ***********
class TemplateWebPlugin : WebServerPluginRegistry {
    // A list of classes that expose web JAX-RS REST APIs.
    override val webApis: List<Function<CordaRPCOps, out Any>> = listOf(Function(::TemplateApi))
    //A list of directories in the resources directory that will be served by Jetty under /web.
    // This template's web frontend is accessible at /web/template.
    override val staticServeDirs: Map<String, String> = mapOf(
            // This will serve the templateWeb directory in resources to /web/template
            "template" to javaClass.classLoader.getResource("templateWeb").toExternalForm()
    )
}

// Serialization whitelist.
class TemplateSerializationWhitelist : SerializationWhitelist {
    override val whitelist: List<Class<*>> = listOf(TemplateData::class.java)
}

// This class is not annotated with @CordaSerializable, so it must be added to the serialization whitelist, above, if
// we want to send it to other nodes within a flow.
data class TemplateData(val payload: String)
