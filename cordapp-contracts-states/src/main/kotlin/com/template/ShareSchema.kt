package com.template

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

object ShareSchema

object ShareSchemaV1: MappedSchema(
        schemaFamily = ShareSchema.javaClass,
        version = 1,
        mappedTypes = listOf(PersistentShare::class.java)) {
    @Entity
    @Table(name = "share_states")
    class PersistentShare(
            @Column(name = "codigo_acao")
            var codigoAcao: String?
    ) : PersistentState() {
        constructor() : this("")
    }
}
