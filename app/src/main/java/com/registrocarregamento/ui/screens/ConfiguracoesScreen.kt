package com.registrocarregamento.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import com.registrocarregamento.ui.components.AppTopBar
import com.registrocarregamento.ui.components.Azul
import com.registrocarregamento.worker.SincronizacaoWorker
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

val Context.dataStore by preferencesDataStore(name = "configuracoes")

val KEY_EMAIL_REMETENTE = stringPreferencesKey("email_remetente")
val KEY_EMAIL_SENHA = stringPreferencesKey("email_senha")
val KEY_EMAIL_DESTINATARIO = stringPreferencesKey("email_destinatario")

@Composable
fun ConfiguracoesScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var remetente by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var destinatario by remember { mutableStateOf("") }
    var salvando by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf<String?>(null) }

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

    Scaffold(
        topBar = {
            AppTopBar(titulo = "Configurações", onBack = { navController.popBackStack() })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Configuração de E-mail",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))

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

            Button(
                onClick = {
                    salvando = true
                    scope.launch {
                        context.dataStore.edit { prefs ->
                            prefs[KEY_EMAIL_REMETENTE] = remetente
                            prefs[KEY_EMAIL_SENHA] = senha
                            prefs[KEY_EMAIL_DESTINATARIO] = destinatario
                        }
                        SincronizacaoWorker.agendar(context, remetente, senha, destinatario)
                        mensagem = "Configurações salvas! Sincronização periódica agendada."
                        salvando = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Azul),
                enabled = remetente.isNotBlank() && senha.isNotBlank() && destinatario.isNotBlank() && !salvando
            ) {
                Icon(Icons.Default.Save, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Salvar configurações")
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        SincronizacaoWorker.dispararImediato(context, remetente, senha, destinatario)
                        mensagem = "Sincronização iniciada!"
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = remetente.isNotBlank() && senha.isNotBlank() && destinatario.isNotBlank()
            ) {
                Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Sincronizar agora")
            }

            mensagem?.let {
                Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
