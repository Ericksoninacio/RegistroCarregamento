package com.registrocarregamento.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.registrocarregamento.data.repository.CarregamentoRepository
import com.registrocarregamento.domain.model.Carregamento
import com.registrocarregamento.util.NetworkUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.util.Properties
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.mail.*
import javax.mail.internet.*

@HiltWorker
class SincronizacaoWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: CarregamentoRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        if (!NetworkUtil.isOnline(applicationContext)) {
            return Result.retry()
        }

        val destinatario = inputData.getString(KEY_EMAIL_DESTINATARIO) ?: return Result.failure(
            workDataOf("erro" to "Destinatário não configurado.")
        )
        val remetente = inputData.getString(KEY_EMAIL_REMETENTE) ?: return Result.failure(
            workDataOf("erro" to "Remetente não configurado.")
        )
        val senha = inputData.getString(KEY_EMAIL_SENHA) ?: return Result.failure(
            workDataOf("erro" to "Senha não configurada.")
        )

        val pendentes = repository.listarPendentes()
        if (pendentes.isEmpty()) {
            return Result.success(workDataOf("resultado" to "Nenhum registro pendente."))
        }

        var enviados = 0
        var erros = 0
        val mensagensErro = mutableListOf<String>()

        for (carregamento in pendentes) {
            val arquivosInvalidos = listarArquivosInvalidos(carregamento)
            if (arquivosInvalidos.isNotEmpty()) {
                val msg = "Registro ${carregamento.id} (${carregamento.placa}): arquivos ausentes — ${arquivosInvalidos.joinToString()}"
                Log.w("SincronizacaoWorker", msg)
                repository.marcarErroSincronizacao(carregamento.id)
                mensagensErro.add(msg)
                erros++
                continue
            }

            try {
                enviarEmail(remetente, senha, destinatario, carregamento)
                repository.marcarComoSincronizado(carregamento.id)
                enviados++
            } catch (e: AuthenticationFailedException) {
                Log.e("SincronizacaoWorker", "Autenticação falhou", e)
                return Result.failure(workDataOf("erro" to "Autenticação falhou. Verifique a Senha de App nas configurações."))
            } catch (e: Exception) {
                Log.e("SincronizacaoWorker", "Erro ao enviar carregamento ${carregamento.id}", e)
                repository.marcarErroSincronizacao(carregamento.id)
                mensagensErro.add("Registro ${carregamento.id}: ${e.message}")
                erros++
            }
        }

        return when {
            erros == 0 -> Result.success(workDataOf("resultado" to "✓ $enviados registro(s) enviado(s)."))
            enviados == 0 -> {
                val erroMsg = mensagensErro.firstOrNull() ?: "Todos os envios falharam."
                Result.failure(workDataOf("erro" to erroMsg))
            }
            else -> Result.success(workDataOf("resultado" to "$enviados enviado(s), $erros com erro. Verifique o histórico."))
        }
    }

    private fun listarArquivosInvalidos(c: Carregamento): List<String> {
        val invalidos = mutableListOf<String>()
        if (c.fotoPlaca.isBlank() || !File(c.fotoPlaca).exists()) invalidos.add("foto da placa")
        if (c.fotoNfe1.isBlank() || !File(c.fotoNfe1).exists()) invalidos.add("foto NF-e 1")
        if (c.fotoCte.isBlank() || !File(c.fotoCte).exists()) invalidos.add("foto CT-e")
        return invalidos
    }

    private fun enviarEmail(
        remetente: String,
        senha: String,
        destinatario: String,
        carregamento: Carregamento
    ) {
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.ssl.trust", "smtp.gmail.com")
            put("mail.smtp.connectiontimeout", "15000")
            put("mail.smtp.timeout", "15000")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication() = PasswordAuthentication(remetente, senha)
        })

        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(remetente, "Registro de Carregamento"))
            addRecipient(Message.RecipientType.TO, InternetAddress(destinatario))
            subject = "[Carregamento] ${carregamento.placa} — ${carregamento.dataRegistro}"
        }

        val cidadeBate = if (carregamento.cidadeConfereCte) "✅ Sim" else "❌ Não"
        val corpo = MimeBodyPart().apply {
            setText("""
                REGISTRO DE CARREGAMENTO
                ========================
                
                Placa:               ${carregamento.placa}
                Cliente:             ${carregamento.cliente}
                Cidade Carregamento: ${carregamento.cidadeCarregamento}
                Cidade bate CT-e:    $cidadeBate
                
                Data: ${carregamento.dataRegistro}
                Hora: ${carregamento.horaRegistro}
                
                ID Registro: ${carregamento.id}
            """.trimIndent())
        }

        val multipart = MimeMultipart().apply {
            addBodyPart(corpo)
            fun anexar(caminho: String, nomeAnexo: String) {
                val f = File(caminho)
                if (f.exists()) {
                    val part = MimeBodyPart()
                    part.attachFile(f)
                    part.fileName = nomeAnexo
                    addBodyPart(part)
                }
            }
            anexar(carregamento.fotoPlaca, "placa.jpg")
            anexar(carregamento.fotoNfe1, "nfe1.jpg")
            carregamento.fotoNfe2?.let { if (it.isNotBlank()) anexar(it, "nfe2.jpg") }
            anexar(carregamento.fotoCte, "cte.jpg")
        }

        message.setContent(multipart)
        Transport.send(message)
    }

    companion object {
        const val KEY_EMAIL_DESTINATARIO = "email_destinatario"
        const val KEY_EMAIL_REMETENTE    = "email_remetente"
        const val KEY_EMAIL_SENHA        = "email_senha"
        const val WORK_NAME              = "sincronizacao_carregamentos"

        // Chamado no App.onCreate — KEEP preserva o timer existente, evitando disparos duplicados
        fun agendar(context: Context, remetente: String, senha: String, destinatario: String) {
            enqueuePeriodicWork(context, remetente, senha, destinatario, ExistingPeriodicWorkPolicy.KEEP)
        }

        // Chamado ao salvar configurações — UPDATE aplica novas credenciais imediatamente
        fun reagendarComNovasConfiguracoes(context: Context, remetente: String, senha: String, destinatario: String) {
            enqueuePeriodicWork(context, remetente, senha, destinatario, ExistingPeriodicWorkPolicy.UPDATE)
        }

        private fun enqueuePeriodicWork(
            context: Context,
            remetente: String,
            senha: String,
            destinatario: String,
            policy: ExistingPeriodicWorkPolicy
        ) {
            val data = workDataOf(
                KEY_EMAIL_REMETENTE    to remetente,
                KEY_EMAIL_SENHA        to senha,
                KEY_EMAIL_DESTINATARIO to destinatario
            )
            val request = PeriodicWorkRequestBuilder<SincronizacaoWorker>(15, TimeUnit.MINUTES)
                .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build())
                .setInputData(data)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(WORK_NAME, policy, request)
        }

        fun dispararImediato(context: Context, remetente: String, senha: String, destinatario: String): UUID {
            val data = workDataOf(
                KEY_EMAIL_REMETENTE    to remetente,
                KEY_EMAIL_SENHA        to senha,
                KEY_EMAIL_DESTINATARIO to destinatario
            )
            val request = OneTimeWorkRequestBuilder<SincronizacaoWorker>()
                .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build())
                .setInputData(data)
                .build()

            WorkManager.getInstance(context).enqueue(request)
            return request.id
        }
    }
}