package com.registrocarregamento.ui.screens

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.registrocarregamento.ui.components.*
import com.registrocarregamento.ui.navigation.Screen
import androidx.compose.foundation.BorderStroke

@Composable
fun ResumoScreen(
    navController: NavController,
    vm: RegistroViewModel = hiltViewModel()
) {
    val rascunho by vm.rascunho.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(titulo = "Resumo do Registro", onBack = { navController.popBackStack() })
        },
        bottomBar = {
            BotoesPrincipais(
                textoSecundario = "← Voltar",
                textoPrimario = "Processar ✓",
                onSecundario = { navController.popBackStack() },
                onPrimario = {
                    vm.processar()
                    navController.navigate(Screen.Processando.route) {
                        popUpTo(Screen.Registro.route) { inclusive = false }
                    }
                }
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
                "Confira os dados antes de processar",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            // Dados do registro
            Surface(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                tonalElevation = 0.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    ResumoLinha("Placa", rascunho.placa)
                    HorizontalDivider(thickness = 0.5.dp)
                    ResumoLinha("Cliente", rascunho.cliente, compact = false)
                    HorizontalDivider(thickness = 0.5.dp)
                    ResumoLinhaVertical("Cidade de Carregamento", rascunho.cidadeCarregamento)
                    HorizontalDivider(thickness = 0.5.dp)
                    ResumoLinhaComBadge("Cidade bate com a CT-e?",
                        if (rascunho.cidadeConfereCte) "Sim, bate" else "Não bate",
                        rascunho.cidadeConfereCte)
                }
            }

            SectionLabel("Documentos")
            Surface(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    DocumentoResumoRow("NFe (Obrigatória)", rascunho.fotoNfe1Path)
                    HorizontalDivider(thickness = 0.5.dp)
                    DocumentoResumoRow("NFe 2 (Opcional)", rascunho.fotoNfe2Path)
                    HorizontalDivider(thickness = 0.5.dp)
                    DocumentoResumoRow("CT-e (Obrigatória)", rascunho.fotoCtePath)
                }
            }
        }
    }
}

@Composable
fun ResumoLinha(label: String, valor: String, compact: Boolean = true) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(valor, fontSize = if (compact) 13.sp else 12.sp, fontWeight = FontWeight.Medium,
            maxLines = 2)
    }
}

@Composable
fun ResumoLinhaVertical(label: String, valor: String) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 14.dp, vertical = 10.dp)) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(valor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ResumoLinhaComBadge(label: String, valor: String, positivo: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Surface(
            shape = RoundedCornerShape(4.dp),
            color = if (positivo) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        ) {
            Text(
                valor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = if (positivo) Color(0xFF1B5E20) else Color(0xFFB71C1C),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
fun DocumentoResumoRow(titulo: String, fotoPath: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(titulo, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (fotoPath != null) {
                AsyncImage(
                    model = Uri.parse(fotoPath),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp).clip(RoundedCornerShape(4.dp)),
                    contentScale = ContentScale.Crop
                )
                Icon(Icons.Default.CheckCircle, null, tint = Verde, modifier = Modifier.size(18.dp))
            } else {
                Text("—", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
            }
        }
    }
}
