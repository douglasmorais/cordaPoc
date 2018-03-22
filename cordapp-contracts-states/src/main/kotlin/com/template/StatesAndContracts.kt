package com.template

import net.corda.core.contracts.*
import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.contracts.ContractState
import net.corda.core.identity.AbstractParty
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
        val command = tx.findCommand<Commands> { true }

        when (command.value) {
            is Commands.Trade -> {
                requireThat {
                    // Constraints on the shape of the transaction
                    "One input should be consumed when trading a share." using (tx.inputs.size == 1)
                    "There should be one output state of type ShareState." using (tx.outputs.size == 1)

                    // Share-specific constraints
                    val input = tx.inputsOfType<ShareState>().single()
                    val out = tx.outputsOfType<ShareState>().single()
                    "The share value must be positive." using (out.shareValue > 0)
                    "The old owner and the new owner cannot be the same entity." using (input.owner != out.owner)

                    // Constraints on the signers
                    "There must be two signers." using (command.signers.toSet().size == 1)
                    "The new owner must be a signer." using (command.signers.containsAll(listOf(out.owner.owningKey)))
                }
            }

            is Commands.ChangeValue -> {
                requireThat {
                    // Constraints on the shape of the transaction
                    "One input should be consumed when changing a share." using (tx.inputs.size == 1)
                    "There should be one output state of type ShareState." using (tx.outputs.size == 1)

                    // Share-specific constraints
                    val input = tx.inputsOfType<ShareState>().single()
                    val out = tx.outputsOfType<ShareState>().single()
                    "The share value must be positive." using (out.shareValue > 0)
                    "The old owner and the new owner must be the same entity." using (input.owner == out.owner)

                    // Constraints on the signers
                    "The new owner must be a signer." using (command.signers.containsAll(listOf(out.owner.owningKey)))
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

                    // Constraints on the signers
                    "There must be one signer." using (command.signers.toSet().size == 1)
                    "The owner must be a signer." using (command.signers.containsAll(listOf(out.owner.owningKey)))
                }
            }
        }
    }

    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        class IPO: TypeOnlyCommandData(), Commands
        class ChangeValue: TypeOnlyCommandData(), Commands
        class Trade: TypeOnlyCommandData(), Commands
    }
}

// *********
// * State *
// *********
class ShareState(val shareValue: Int,
                 val owner: Party,
                 val codigoAcao: String) : ContractState {
    override val participants get() = listOf(owner)
}





