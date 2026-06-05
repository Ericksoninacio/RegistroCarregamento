package com.registrocarregamento.domain.model

data class Carregamento(
    val id: Long = 0,
    val placa: String,
    val cliente: String,
    val cidadeCarregamento: String,
    val cidadeConfereCte: Boolean,
    val dataRegistro: String,
    val horaRegistro: String,
    val fotoPlaca: String,
    val fotoNfe1: String,
    val fotoNfe2: String? = null,
    val fotoCte: String,
    val sincronizado: Boolean = false,
    val criadoEm: Long = System.currentTimeMillis()
)

data class CarregamentoRascunho(
    val placa: String = "",
    val cliente: String = "",
    val cidadeCarregamento: String = "",
    val cidadeConfereCte: Boolean = true,
    val fotoPlacaPath: String? = null,
    val fotoNfe1Path: String? = null,
    val fotoNfe2Path: String? = null,
    val fotoCtePath: String? = null,
)
