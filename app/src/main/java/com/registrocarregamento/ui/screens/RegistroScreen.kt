package com.registrocarregamento.ui.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.registrocarregamento.ui.components.*
import com.registrocarregamento.ui.navigation.Screen

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RegistroScreen(
    navController: NavController,
    vm: RegistroViewModel = hiltViewModel()
) {
    val rascunho by vm.rascunho.collectAsState()
    val pendentes by vm.pendentes.collectAsState()
    val context = LocalContext.current
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    var mostrarCamera by remember { mutableStateOf(false) }
    var mostrarDialogPlaca by remember { mutableStateOf(false) }
    var edicaoPlaca by remember { mutableStateOf("") }

    if (mostrarCamera) {
        CameraScreen(
            titulo = "Fotografar Placa",
            nomeFoto = "placa",
            placa = rascunho.placa.ifBlank { "temp" },
            detectarPlaca = true,
            onFotoCapturada = { path, placaDetectada ->
                vm.atualizarFotoPlaca(path)
                placaDetectada?.let { vm.atualizarPlaca(it) }
                mostrarCamera = false
                if (placaDetectada == null) {
                    mostrarDialogPlaca = true
                    edicaoPlaca = rascunho.placa
                }
            },
            onFechar = { mostrarCamera = false }
        )
        return
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Registro de Carregamento",
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Historico.route) }) {
                        Icon(Icons.Default.History, "Histórico", tint = Color.White)
                    }
                    IconButton(onClick = { navController.navigate(Screen.Configuracoes.route) }) {
                        Icon(Icons.Default.Settings, "Configurações", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            BotoesPrincipais(
                textoSecundario = "Limpar tudo",
                textoPrimario = "Próximo →",
                onSecundario = { vm.limparTudo() },
                onPrimario = { navController.navigate(Screen.Documentos.route) },
                habilitadoPrimario = rascunho.placa.isNotBlank() &&
                        rascunho.cliente.isNotBlank() &&
                        rascunho.cidadeCarregamento.isNotBlank()
            )
        },
        // imePadding no Scaffold garante que o conteúdo sobe junto com o teclado
        contentWindowInsets = WindowInsets.ime
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding() // empurra o conteúdo acima do teclado
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            BadgeOffline(pendentes)

            // Placa
            SectionLabel("1. Placa do Veículo")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .clickable {
                        if (cameraPermission.status.isGranted) mostrarCamera = true
                        else cameraPermission.launchPermissionRequest()
                    }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AzulClaro),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, null, tint = Azul, modifier = Modifier.size(24.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Fotografar placa", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    Text(
                        "Aponte a câmera para a placa do veículo",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Icon(
                    Icons.Default.ChevronRight, null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }

            if (rascunho.fotoPlacaPath != null || rascunho.placa.isNotBlank()) {
                SectionLabel("Placa reconhecida")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE3F2FD))
                        .border(0.5.dp, Color(0xFF90CAF9), RoundedCornerShape(8.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        rascunho.placa.ifBlank { "—" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF0D47A1),
                        letterSpacing = 1.sp
                    )
                    IconButton(onClick = { edicaoPlaca = rascunho.placa; mostrarDialogPlaca = true }) {
                        Icon(Icons.Default.Edit, "Editar", tint = Azul)
                    }
                }
            }

            // Cliente
            SectionLabel("2. Cliente")
            OutlinedTextField(
                value = rascunho.cliente,
                onValueChange = vm::atualizarCliente,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Nome do cliente / transportadora") },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                singleLine = true,
                trailingIcon = {
                    if (rascunho.cliente.isNotBlank()) {
                        IconButton(onClick = { vm.atualizarCliente("") }) {
                            Icon(Icons.Default.Cancel, null)
                        }
                    }
                }
            )

            // Cidade
            SectionLabel("3. Cidade de Carregamento")
            OutlinedTextField(
                value = rascunho.cidadeCarregamento,
                onValueChange = vm::atualizarCidade,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cidade - UF") },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                singleLine = true,
                trailingIcon = {
                    if (rascunho.cidadeCarregamento.isNotBlank()) {
                        IconButton(onClick = { vm.atualizarCidade("") }) {
                            Icon(Icons.Default.Cancel, null)
                        }
                    }
                }
            )

            // Espaço extra no final para o campo não ficar colado no bottomBar ao rolar
            Spacer(Modifier.height(8.dp))
        }
    }

    if (mostrarDialogPlaca) {
        AlertDialog(
            onDismissRequest = { mostrarDialogPlaca = false },
            title = { Text("Corrigir placa") },
            text = {
                OutlinedTextField(
                    value = edicaoPlaca,
                    onValueChange = { edicaoPlaca = it.uppercase() },
                    label = { Text("Placa") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.atualizarPlaca(edicaoPlaca)
                    mostrarDialogPlaca = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogPlaca = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun SectionLabel(texto: String) {
    Text(
        texto,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        letterSpacing = 0.5.sp
    )
}