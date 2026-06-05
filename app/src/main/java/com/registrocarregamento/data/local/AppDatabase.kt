package com.registrocarregamento.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.registrocarregamento.data.local.dao.CarregamentoDao
import com.registrocarregamento.data.local.dao.FilaSincronizacaoDao
import com.registrocarregamento.data.local.entity.CarregamentoEntity
import com.registrocarregamento.data.local.entity.FilaSincronizacaoEntity

@Database(
    entities = [CarregamentoEntity::class, FilaSincronizacaoEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun carregamentoDao(): CarregamentoDao
    abstract fun filaSincronizacaoDao(): FilaSincronizacaoDao
}
