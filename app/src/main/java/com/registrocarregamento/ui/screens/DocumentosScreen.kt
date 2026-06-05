package com.registrocarregamento.ui.screens

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun DocumentosScreen(
    navController: NavController,
    vm: RegistroViewModel = hiltViewModel()
) {
    val rascunho by vm.rascunho.collectAsState()
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    var docAtivo by remember { mutableStateOf<String?>(null) }

    if (docAtivo != null) {
        val (titulo, nome) = when (docAtivo) {
            "nfe1" -> "NFe (Obrigatória)" to "nfe1"
            "nfe2" -> "NFe 2 (Opcional)" to "nfe2"
            "cte" -> "CT-e (Obrigatória)" to "cte"
            else -> return
        }
        CameraScreen(
            titulo = "Fotografar $titulo",
            nomeFoto = nome,
            placa = rascunho.placa.ifBlank { "temp" },
            onFotoCapturada = { path, _ ->
                when (docAtivo) {
                    "nfe1" -> vm.atualizarFotoNfe1(path)
                    "nfe2" -> vm.atualizarFotoNfe2(path)
                    "cte" -> vm.atualizarFotoCte(path)
                }
                docAtivo = null
            },
            onFechar = { docAtivo = null }
        )
        return
    }

    val podeProsseguir = rascunho.fotoNfe1Path != null && rascunho.fotoCtePath != null

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Documentos",
                subtitulo = "4. Documentos",
                onBack = { navController.popBackStack() }
            )
        },
        bottomBar = {
            BotoesPrincipais(
                textoSecundario = "← Voltar",
                textoPrimario = "Próximo →",
                onSecundario = { navController.popBackStack() },
                onPrimario = { navController.navigate(Screen.Validacao.route) },
                habilitadoPrimario = podeProsseguir
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Fotografe os documentos solicitados",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            DocumentoRow(
                titulo = "NFe",
                obrigatorio = true,
                fotoPath = rascunho.fotoNfe1Path,
                onClick = {
                    if (cameraPermission.status.isGranted) docAtivo = "nfe1"
                    else cameraPermission.launchPermissionRequest()
                }
            )

            DocumentoRow(
                titulo = "NFe 2",
                obrigatorio = false,
                fotoPath = rascunho.fotoNfe2Path,
                onClick = {
                    if (cameraPermission.status.isGranted) docAtivo = "nfe2"
                    else cameraPermission.launchPermissionRequest()
                }
            )

            DocumentoRow(
                titulo = "CT-e",
                obrigatorio = true,
                fotoPath = rascunho.fotoCtePath,
                onClick = {
                    if (cameraPermission.status.isGranted) docAtivo = "cte"
                    else cameraPermission.launchPermissionRequest()
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AzulClaro)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(Icons.Default.Info, null, tint = Azul, modifier = Modifier.size(18.dp))
                Text(
                    "Enquadre o documento e verifique se todas as informações estão legíveis.",
                    fontSize = 12.sp,
                    color = Azul.copy(alpha = 0.9f),
                    lineHeight = 18.sp
                )
            }

            if (!podeProsseguir) {
                Text(
                    "* NFe e CT-e são obrigatórios para prosseguir.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
