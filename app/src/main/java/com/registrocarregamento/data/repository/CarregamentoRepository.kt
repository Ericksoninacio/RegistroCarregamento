package com.registrocarregamento.data.repository

import com.registrocarregamento.data.local.dao.CarregamentoDao
import com.registrocarregamento.data.local.dao.FilaSincronizacaoDao
import com.registrocarregamento.data.local.entity.CarregamentoEntity
import com.registrocarregamento.data.local.entity.FilaSincronizacaoEntity
import com.registrocarregamento.domain.model.Carregamento
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CarregamentoRepository @Inject constructor(
    private val carregamentoDao: CarregamentoDao,
    private val filaSincronizacaoDao: FilaSincronizacaoDao
) {
    fun listarTodos(): Flow<List<Carregamento>> =
        carregamentoDao.listarTodos().map { list -> list.map { it.toDomain() } }

    fun contarPendentes(): Flow<Int> = carregamentoDao.contarPendentes()

    suspend fun salvar(carregamento: Carregamento): Long {
        val entity = carregamento.toEntity()
        val id = carregamentoDao.inserir(entity)
        filaSincronizacaoDao.inserir(FilaSincronizacaoEntity(carregamentoId = id))
        return id
    }

    suspend fun listarPendentes(): List<Carregamento> =
        carregamentoDao.listarPendentes().map { it.toDomain() }

    suspend fun buscarPorId(id: Long): Carregamento? =
        carregamentoDao.buscarPorId(id)?.toDomain()

    suspend fun marcarComoSincronizado(id: Long) {
        carregamentoDao.marcarComoSincronizado(id)
        filaSincronizacaoDao.atualizarStatus(id, "ENVIADO")
    }

    suspend fun marcarErroSincronizacao(id: Long) {
        filaSincronizacaoDao.atualizarStatus(id, "ERRO")
    }

    private fun CarregamentoEntity.toDomain() = Carregamento(
        id, placa, cliente, cidadeCarregamento, cidadeConfereCte,
        dataRegistro, horaRegistro, fotoPlaca, fotoNfe1, fotoNfe2,
        fotoCte, sincronizado, criadoEm
    )

    private fun Carregamento.toEntity() = CarregamentoEntity(
        id, placa, cliente, cidadeCarregamento, cidadeConfereCte,
        dataRegistro, horaRegistro, fotoPlaca, fotoNfe1, fotoNfe2,
        fotoCte, sincronizado, criadoEm
    )
}
