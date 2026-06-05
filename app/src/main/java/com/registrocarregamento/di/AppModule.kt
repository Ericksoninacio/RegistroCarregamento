package com.registrocarregamento.di

import android.content.Context
import androidx.room.Room
import com.registrocarregamento.data.local.AppDatabase
import com.registrocarregamento.data.local.dao.CarregamentoDao
import com.registrocarregamento.data.local.dao.FilaSincronizacaoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "registro_carregamento.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideCarregamentoDao(db: AppDatabase): CarregamentoDao = db.carregamentoDao()

    @Provides
    fun provideFilaSincronizacaoDao(db: AppDatabase): FilaSincronizacaoDao = db.filaSincronizacaoDao()
}
