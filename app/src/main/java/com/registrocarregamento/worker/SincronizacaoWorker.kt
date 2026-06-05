package com.registrocarregamento.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.registrocarregamento.data.repository.CarregamentoRepository
import com.registrocarregamento.util.NetworkUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.util.Properties
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

        val destinatario = inputData.getString(KEY_EMAIL_DESTINATARIO) ?: return Result.failure()
        val remetente = inputData.getString(KEY_EMAIL_REMETENTE) ?: return Result.failure()
        val senha = inputData.getString(KEY_EMAIL_SENHA) ?: return Result.failure()

        val pendentes = repository.listarPendentes()
        if (pendentes.isEmpty()) return Result.success()

        var erros = 0
        for (carregamento in pendentes) {
            try {
                enviarEmail(
                    remetente = remetente,
                    senha = senha,
                    destinatario = destinatario,
                    carregamento = com.registrocarregamento.domain.model.Carregamento(
                        id = carregamento.id,
                        placa = carregamento.placa,
                        cliente = carregamento.cliente,
                        cidadeCarregamento = carregamento.cidadeCarregamento,
                        cidadeConfereCte = carregamento.cidadeConfereCte,
                        dataRegistro = carregamento.dataRegistro,
                        horaRegistro = carregamento.horaRegistro,
                        fotoPlaca = carregamento.fotoPlaca,
                        fotoNfe1 = carregamento.fotoNfe1,
                        fotoNfe2 = carregamento.fotoNfe2,
                        fotoCte = carregamento.fotoCte
                    )
                )
                repository.marcarComoSincronizado(carregamento.id)
            } catch (e: Exception) {
                Log.e("SincronizacaoWorker", "Erro ao enviar carregamento ${carregamento.id}", e)
                repository.marcarErroSincronizacao(carregamento.id)
                erros++
            }
        }

        return if (erros == 0) Result.success() else Result.retry()
    }

    private fun enviarEmail(
        remetente: String,
        senha: String,
        destinatario: String,
        carregamento: com.registrocarregamento.domain.model.Carregamento
    ) {
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.ssl.trust", "smtp.gmail.com")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication() =
                PasswordAuthentication(remetente, senha)
        })

        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(remetente, "Registro de Carregamento"))
            addRecipient(Message.RecipientType.TO, InternetAddress(destinatario))
            subject = "[Carregamento] ${carregamento.placa} — ${carregamento.dataRegistro}"
        }

        val corpo = MimeBodyPart().apply {
            val cidadeBate = if (carregamento.cidadeConfereCte) "✅ Sim" else "❌ Não"
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
            carregamento.fotoNfe2?.let { anexar(it, "nfe2.jpg") }
            anexar(carregamento.fotoCte, "cte.jpg")
        }

        message.setContent(multipart)
        Transport.send(message)
    }

    companion object {
        const val KEY_EMAIL_DESTINATARIO = "email_destinatario"
        const val KEY_EMAIL_REMETENTE = "email_remetente"
        const val KEY_EMAIL_SENHA = "email_senha"
        const val WORK_NAME = "sincronizacao_carregamentos"

        fun agendar(context: Context, remetente: String, senha: String, destinatario: String) {
            val data = workDataOf(
                KEY_EMAIL_REMETENTE to remetente,
                KEY_EMAIL_SENHA to senha,
                KEY_EMAIL_DESTINATARIO to destinatario
            )
            val request = PeriodicWorkRequestBuilder<SincronizacaoWorker>(1, TimeUnit.MINUTES)
                .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build())
                .setInputData(data)
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 1, TimeUnit.MINUTES)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        fun dispararImediato(context: Context, remetente: String, senha: String, destinatario: String) {
            val data = workDataOf(
                KEY_EMAIL_REMETENTE to remetente,
                KEY_EMAIL_SENHA to senha,
                KEY_EMAIL_DESTINATARIO to destinatario
            )
            val request = OneTimeWorkRequestBuilder<SincronizacaoWorker>()
                .setConstraints(Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build())
                .setInputData(data)
                .build()

            WorkManager.getInstance(context).enqueue(request)
        }
    }
}
