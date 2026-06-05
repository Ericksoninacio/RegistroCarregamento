package com.registrocarregamento.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carregamentos")
data class CarregamentoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val placa: String,
    val cliente: String,
    val cidadeCarregamento: String,
    val cidadeConfereCte: Boolean,
    val dataRegistro: String,
    val horaRegistro: String,
    val fotoPlaca: String,
    val fotoNfe1: String,
    val fotoNfe2: String?,
    val fotoCte: String,
    val sincronizado: Boolean = false,
    val criadoEm: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "fila_sincronizacao",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = CarregamentoEntity::class,
            parentColumns = ["id"],
            childColumns = ["carregamentoId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ]
)
data class FilaSincronizacaoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val carregamentoId: Long,
    val tentativas: Int = 0,
    val ultimaTentativa: Long? = null,
    val status: String = "PENDENTE" // PENDENTE | ENVIANDO | ENVIADO | ERRO
)
