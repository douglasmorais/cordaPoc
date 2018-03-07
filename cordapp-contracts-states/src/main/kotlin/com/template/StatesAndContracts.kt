package com.template

import net.corda.core.contracts.*
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.Party
import net.corda.core.transactions.LedgerTransaction

// *****************
// * Contract Code *
// *****************
// This is used to identify our contract when building a transaction
val SHARE_CONTRACT_ID = "com.template.ShareContract"

class ShareContract : Contract {
    // Our Create command
    class Create : CommandData

    override fun verify(tx: LedgerTransaction) {
//        val command = tx.commands.requireSingleCommand<Create>()
        val command = tx.findCommand<Commands> { true }

        when (command.value) {
            is Commands.Trade -> {
                requireThat {
                    // Constraints on the shape of the transaction
                    "No inputs should be consumed when trading a share." using (tx.inputs.isEmpty())
                    "There should be one output state of type ShareState." using (tx.outputs.size == 1)

                    // Share-specific constraints
                    val out = tx.outputsOfType<ShareState>().single()
                    "The share value must be positive." using (out.shareValue > 0)
                    "The seller and the buyer cannot be the same entity." using (out.seller != out.buyer)

                    // Constraints on the signers
                    "There must be two signers." using (command.signers.toSet().size == 2)
                    "The seller and the buyer must be signers." using (command.signers.containsAll(listOf(out.seller.owningKey, out.buyer.owningKey)))
                }
            }

            is Commands.Change -> {
                requireThat {
                    // Constraints on the shape of the transaction
                    // TODO: Como fazer "tx.inputs.size == 1" funcionar?
//                    "One input should be consumed when changing a share." using (tx.inputs.size == 1)
                    "No inputs should be consumed when changing a share." using (tx.inputs.isEmpty())
                    "There should be one output state of type ShareState." using (tx.outputs.size == 1)
//                    val input = tx.inputsOfType<ShareState>().single()
//                    val output = tx.outputsOfType<ShareState>().single()
//                    "The field 'codigoAcao' must be the same." using (input.codigoAcao == output.codigoAcao)

                    // Share-specific constraints
                    val out = tx.outputsOfType<ShareState>().single()
                    "The share value must be positive." using (out.shareValue > 0)

                    // Constraints on the signers
                    // TODO: A regra de um assinante trava a transação com enterprise = outro nó!
//                    "There must be one signer." using (command.signers.toSet().size == 1)
                    "The exchange must be a signer." using (command.signers.containsAll(listOf(out.buyer.owningKey)))
                }
            }

            is Commands.IPO -> {
                requireThat {
                    // Constraints on the shape of the transaction
                    "No inputs should be consumed in an IPO." using (tx.inputs.isEmpty())
                    "There should be one output state of type ShareState." using (tx.outputs.size == 1)

                    // Share-specific constraints
                    val out = tx.outputsOfType<ShareState>().single()
                    "The share value must be positive." using (out.shareValue > 0)
                    "The seller and the buyer must be the same entity." using (out.seller == out.buyer)

                    // Constraints on the signers
                    // TODO: A regra de dois assinantes trava a transação!
                    "There must be one signer." using (command.signers.toSet().size == 1)
                    "The enterprise must a signer." using (command.signers.containsAll(listOf(out.seller.owningKey)))
                }
            }
        }
    }

    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        class IPO: TypeOnlyCommandData(), Commands
        class Change: TypeOnlyCommandData(), Commands
        class Trade: TypeOnlyCommandData(), Commands
    }
}

// *********
// * State *
// *********
class ShareState(val shareValue: Int,
                 val seller: Party,
                 val buyer: Party,
                 val codigoAcao: String?) : ContractState {
    override val participants get() = listOf(seller, buyer)
}
