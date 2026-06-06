package com.registrocarregamento.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.registrocarregamento.ui.components.AppTopBar
import com.registrocarregamento.ui.components.Azul
import com.registrocarregamento.worker.SincronizacaoWorker
import com.registrocarregamento.worker.TesteEmailWorker
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "configuracoes")

val KEY_EMAIL_REMETENTE   = stringPreferencesKey("email_remetente")
val KEY_EMAIL_SENHA        = stringPreferencesKey("email_senha")
val KEY_EMAIL_DESTINATARIO = stringPreferencesKey("email_destinatario")

// Estados possíveis para os botões com feedback
enum class StatusEnvio { IDLE, ENVIANDO, SUCESSO, ERRO }

@Composable
fun ConfiguracoesScreen(navController: NavController) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    var remetente   by remember { mutableStateOf("") }
    var senha       by remember { mutableStateOf("") }
    var destinatario by remember { mutableStateOf("") }
    var salvando    by remember { mutableStateOf(false) }
    var mensagemSalvar by remember { mutableStateOf<String?>(null) }

    var statusTeste by remember { mutableStateOf(StatusEnvio.IDLE) }
    var statusSinc  by remember { mutableStateOf(StatusEnvio.IDLE) }
    var mensagemErro by remember { mutableStateOf<String?>(null) }

    // Carrega configurações salvas
    LaunchedEffect(Unit) {
        context.dataStore.data.map { prefs ->
            Triple(
                prefs[KEY_EMAIL_REMETENTE] ?: "",
                prefs[KEY_EMAIL_SENHA] ?: "",
                prefs[KEY_EMAIL_DESTINATARIO] ?: ""
            )
        }.collect { (r, s, d) ->
            remetente = r; senha = s; destinatario = d
        }
    }

    val camposPreenchidos = remetente.isNotBlank() && senha.isNotBlank() && destinatario.isNotBlank()

    Scaffold(
        topBar = { AppTopBar(titulo = "Configurações", onBack = { navController.popBackStack() }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Configuração de E-mail",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                "Use uma Senha de App do Gmail (não a senha normal). Ative em: " +
                        "Conta Google → Segurança → Verificação em 2 etapas → Senhas de app.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                lineHeight = 18.sp
            )

            OutlinedTextField(
                value = remetente,
                onValueChange = { remetente = it },
                label = { Text("E-mail remetente (Gmail)") },
                placeholder = { Text("seuemail@gmail.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            OutlinedTextField(
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha de App") },
                placeholder = { Text("xxxx xxxx xxxx xxxx") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            OutlinedTextField(
                value = destinatario,
                onValueChange = { destinatario = it },
                label = { Text("E-mail destinatário") },
                placeholder = { Text("gestor@empresa.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            // Salvar
            Button(
                onClick = {
                    salvando = true
                    mensagemSalvar = null
                    scope.launch {
                        context.dataStore.edit { prefs ->
                            prefs[KEY_EMAIL_REMETENTE]   = remetente
                            prefs[KEY_EMAIL_SENHA]        = senha
                            prefs[KEY_EMAIL_DESTINATARIO] = destinatario
                        }
                        SincronizacaoWorker.reagendarComNovasConfiguracoes(context, remetente, senha, destinatario)
                        mensagemSalvar = "Configurações salvas!"
                        salvando = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Azul),
                enabled = camposPreenchidos && !salvando
            ) {
                if (salvando) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(6.dp))
                Text(if (salvando) "Salvando..." else "Salvar configurações")
            }
            mensagemSalvar?.let {
                Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            }

            HorizontalDivider()

            // Enviar e-mail de teste
            OutlinedButton(
                onClick = {
                    statusTeste = StatusEnvio.ENVIANDO
                    mensagemErro = null
                    val workId = TesteEmailWorker.disparar(context, remetente, senha, destinatario)
                    // Observa o resultado em tempo real
                    WorkManager.getInstance(context)
                        .getWorkInfoByIdLiveData(workId)
                        .observeForever { info ->
                            when (info?.state) {
                                WorkInfo.State.SUCCEEDED -> statusTeste = StatusEnvio.SUCESSO
                                WorkInfo.State.FAILED    -> {
                                    statusTeste = StatusEnvio.ERRO
                                    mensagemErro = info.outputData.getString("erro")
                                        ?: "Falha ao enviar. Verifique e-mail e senha de app."
                                }
                                else -> Unit
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = camposPreenchidos && statusTeste != StatusEnvio.ENVIANDO
            ) {
                when (statusTeste) {
                    StatusEnvio.ENVIANDO -> CircularProgressIndicator(
                        modifier = Modifier.size(16.dp), strokeWidth = 2.dp
                    )
                    StatusEnvio.SUCESSO  -> Icon(Icons.Default.CheckCircle, null,
                        tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                    StatusEnvio.ERRO     -> Icon(Icons.Default.Error, null,
                        tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    else -> Icon(Icons.Default.Science, null, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(6.dp))
                Text(when (statusTeste) {
                    StatusEnvio.ENVIANDO -> "Enviando e-mail de teste..."
                    StatusEnvio.SUCESSO  -> "E-mail de teste enviado!"
                    StatusEnvio.ERRO     -> "Falhou — tentar novamente"
                    else                 -> "Enviar e-mail de teste"
                })
            }

            // Mensagem de erro do teste
            mensagemErro?.let {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.Error,
                            null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp).padding(top = 1.dp)
                        )
                        Text(
                            it,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Sincronizar pendentes agora
            OutlinedButton(
                onClick = {
                    statusSinc = StatusEnvio.ENVIANDO
                    val workId = SincronizacaoWorker.dispararImediato(context, remetente, senha, destinatario)
                    WorkManager.getInstance(context)
                        .getWorkInfoByIdLiveData(workId)
                        .observeForever { info ->
                            when (info?.state) {
                                WorkInfo.State.SUCCEEDED -> {
                                    statusSinc = StatusEnvio.SUCESSO
                                    val resultado = info.outputData.getString("resultado")
                                    if (resultado != null) mensagemErro = resultado
                                }
                                WorkInfo.State.FAILED -> {
                                    statusSinc = StatusEnvio.ERRO
                                    mensagemErro = info.outputData.getString("erro")
                                        ?: "Erro ao sincronizar. Verifique as configurações."
                                }
                                else -> Unit
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = camposPreenchidos && statusSinc != StatusEnvio.ENVIANDO
            ) {
                when (statusSinc) {
                    StatusEnvio.ENVIANDO -> CircularProgressIndicator(
                        modifier = Modifier.size(16.dp), strokeWidth = 2.dp
                    )
                    StatusEnvio.SUCESSO  -> Icon(Icons.Default.CheckCircle, null,
                        tint = Color(0xFF4CAF50), modifier = Modifier.size(16.dp))
                    StatusEnvio.ERRO     -> Icon(Icons.Default.Error, null,
                        tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    else -> Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(6.dp))
                Text(when (statusSinc) {
                    StatusEnvio.ENVIANDO -> "Sincronizando..."
                    StatusEnvio.SUCESSO  -> "Sincronização concluída!"
                    StatusEnvio.ERRO     -> "Erro na sincronização"
                    else                 -> "Sincronizar registros pendentes"
                })
            }
        }
    }
}