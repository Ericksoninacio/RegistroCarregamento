package com.registrocarregamento.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Properties
import java.util.UUID
import javax.mail.*
import javax.mail.internet.*

@HiltWorker
class TesteEmailWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val remetente   = inputData.getString(KEY_REMETENTE)   ?: return Result.failure()
        val senha       = inputData.getString(KEY_SENHA)        ?: return Result.failure()
        val destinatario = inputData.getString(KEY_DESTINATARIO) ?: return Result.failure()

        return try {
            enviarEmailTeste(remetente, senha, destinatario)
            Result.success()
        } catch (e: AuthenticationFailedException) {
            Result.failure(workDataOf("erro" to "Autenticação falhou. Verifique se está usando a Senha de App correta."))
        } catch (e: MessagingException) {
            Result.failure(workDataOf("erro" to "Erro de conexão: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(workDataOf("erro" to "Erro inesperado: ${e.message}"))
        }
    }

    private fun enviarEmailTeste(remetente: String, senha: String, destinatario: String) {
        val props = Properties().apply {
            put("mail.smtp.auth", "true")
            put("mail.smtp.starttls.enable", "true")
            put("mail.smtp.host", "smtp.gmail.com")
            put("mail.smtp.port", "587")
            put("mail.smtp.ssl.trust", "smtp.gmail.com")
            put("mail.smtp.connectiontimeout", "10000")
            put("mail.smtp.timeout", "10000")
        }

        val session = Session.getInstance(props, object : Authenticator() {
            override fun getPasswordAuthentication() = PasswordAuthentication(remetente, senha)
        })

        val message = MimeMessage(session).apply {
            setFrom(InternetAddress(remetente, "Registro de Carregamento"))
            addRecipient(Message.RecipientType.TO, InternetAddress(destinatario))
            subject = "[Teste] Configuração de e-mail funcionando ✓"
            setText("""
                Este é um e-mail de teste do app Registro de Carregamento.
                
                Se você recebeu esta mensagem, a configuração está correta
                e os registros serão enviados normalmente.
                
                Remetente configurado: $remetente
            """.trimIndent())
        }

        Transport.send(message)
    }

    companion object {
        const val KEY_REMETENTE    = "remetente"
        const val KEY_SENHA         = "senha"
        const val KEY_DESTINATARIO  = "destinatario"

        fun disparar(
            context: Context,
            remetente: String,
            senha: String,
            destinatario: String
        ): UUID {
            val request = OneTimeWorkRequestBuilder<TesteEmailWorker>()
                .setInputData(workDataOf(
                    KEY_REMETENTE   to remetente,
                    KEY_SENHA        to senha,
                    KEY_DESTINATARIO to destinatario
                ))
                .build()
            WorkManager.getInstance(context).enqueue(request)
            return request.id
        }
    }
}