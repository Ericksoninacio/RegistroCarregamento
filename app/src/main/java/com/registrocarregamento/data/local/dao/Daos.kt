package com.registrocarregamento.data.local.dao

import androidx.room.*
import com.registrocarregamento.data.local.entity.CarregamentoEntity
import com.registrocarregamento.data.local.entity.FilaSincronizacaoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CarregamentoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(carregamento: CarregamentoEntity): Long

    @Update
    suspend fun atualizar(carregamento: CarregamentoEntity)

    @Query("SELECT * FROM carregamentos ORDER BY criadoEm DESC")
    fun listarTodos(): Flow<List<CarregamentoEntity>>

    @Query("SELECT * FROM carregamentos WHERE sincronizado = 0 ORDER BY criadoEm ASC")
    suspend fun listarPendentes(): List<CarregamentoEntity>

    @Query("SELECT * FROM carregamentos WHERE id = :id")
    suspend fun buscarPorId(id: Long): CarregamentoEntity?

    @Query("UPDATE carregamentos SET sincronizado = 1 WHERE id = :id")
    suspend fun marcarComoSincronizado(id: Long)

    @Query("SELECT COUNT(*) FROM carregamentos WHERE sincronizado = 0")
    fun contarPendentes(): Flow<Int>

    @Query("DELETE FROM carregamentos")
    suspend fun apagarTodos()
}

@Dao
interface FilaSincronizacaoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(fila: FilaSincronizacaoEntity): Long

    @Query("SELECT * FROM fila_sincronizacao WHERE status IN ('PENDENTE', 'ERRO') ORDER BY id ASC")
    suspend fun listarParaEnvio(): List<FilaSincronizacaoEntity>

    @Query("UPDATE fila_sincronizacao SET status = :status, tentativas = tentativas + 1, ultimaTentativa = :agora WHERE carregamentoId = :carregamentoId")
    suspend fun atualizarStatus(carregamentoId: Long, status: String, agora: Long = System.currentTimeMillis())

    @Query("DELETE FROM fila_sincronizacao WHERE carregamentoId = :carregamentoId")
    suspend fun remover(carregamentoId: Long)
}
