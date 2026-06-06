package com.registrocarregamento.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.registrocarregamento.domain.model.Carregamento
import com.registrocarregamento.ui.components.AppTopBar
import com.registrocarregamento.ui.components.Verde

@Composable
fun HistoricoScreen(
    navController: NavController,
    vm: RegistroViewModel = hiltViewModel()
) {
    val historico by vm.historico.collectAsState()
    var confirmarApagar by remember { mutableStateOf(false) }

    if (confirmarApagar) {
        AlertDialog(
            onDismissRequest = { confirmarApagar = false },
            title = { Text("Apagar histórico") },
            text = {
                Text(
                    "Todos os ${historico.size} registro(s) serão removidos permanentemente. " +
                            "Esta ação não pode ser desfeita.",
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    vm.apagarHistorico()
                    confirmarApagar = false
                }) {
                    Text("Apagar tudo", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarApagar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Histórico",
                onBack = { navController.popBackStack() },
                actions = {
                    if (historico.isNotEmpty()) {
                        IconButton(onClick = { confirmarApagar = true }) {
                            Icon(
                                Icons.Default.DeleteSweep,
                                contentDescription = "Apagar tudo",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (historico.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Nenhum registro encontrado.",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(historico, key = { it.id }) { c ->
                    HistoricoItem(c)
                }
            }
        }
    }
}

@Composable
fun HistoricoItem(c: Carregamento) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
        tonalElevation = 0.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        c.placa.ifBlank { "—" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                    Text(
                        c.cliente.ifBlank { "—" },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        if (c.sincronizado) Icons.Default.CloudDone else Icons.Default.CloudOff,
                        null,
                        tint = if (c.sincronizado) Verde else Color(0xFFE65100),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        if (c.sincronizado) "Enviado" else "Pendente",
                        fontSize = 11.sp,
                        color = if (c.sincronizado) Verde else Color(0xFFE65100),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            Spacer(Modifier.height(6.dp))
            HorizontalDivider(thickness = 0.5.dp)
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${c.dataRegistro}  ${c.horaRegistro}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    c.cidadeCarregamento.ifBlank { "—" },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}