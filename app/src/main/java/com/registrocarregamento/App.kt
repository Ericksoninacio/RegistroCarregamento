package com.registrocarregamento

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.WorkManager
import com.registrocarregamento.ui.screens.KEY_EMAIL_DESTINATARIO
import com.registrocarregamento.ui.screens.KEY_EMAIL_REMETENTE
import com.registrocarregamento.ui.screens.KEY_EMAIL_SENHA
import com.registrocarregamento.ui.screens.dataStore
import com.registrocarregamento.worker.SincronizacaoWorker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        reagendarSincronizacao()
    }

    private fun reagendarSincronizacao() {
        CoroutineScope(Dispatchers.IO).launch {
            val prefs = dataStore.data.first()
            val remetente    = prefs[KEY_EMAIL_REMETENTE]    ?: return@launch
            val senha        = prefs[KEY_EMAIL_SENHA]         ?: return@launch
            val destinatario = prefs[KEY_EMAIL_DESTINATARIO]  ?: return@launch

            if (remetente.isBlank() || senha.isBlank() || destinatario.isBlank()) return@launch

            SincronizacaoWorker.agendar(this@App, remetente, senha, destinatario)
        }
    }
}