package com.registrocarregamento.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.registrocarregamento.ui.components.Azul
import com.registrocarregamento.ui.components.AzulClaro
import com.registrocarregamento.ui.components.Verde
import com.registrocarregamento.ui.components.VerdeClaro
import com.registrocarregamento.ui.navigation.Screen
import com.registrocarregamento.util.DateUtil
import kotlinx.coroutines.delay
import androidx.compose.foundation.BorderStroke

@Composable
fun ProcessandoScreen(
    navController: NavController,
    vm: RegistroViewModel = hiltViewModel()
) {
    val idSalvo by vm.idSalvo.collectAsState()
    var progresso by remember { mutableFloatStateOf(0f) }

    val animProgresso by animateFloatAsState(
        targetValue = progresso,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "progresso"
    )

    LaunchedEffect(Unit) {
        delay(300); progresso = 0.33f
        delay(600); progresso = 0.66f
        delay(600); progresso = 1f
        delay(500)
        val id = idSalvo ?: 0L
        navController.navigate(Screen.Concluido.withId(id)) {
            popUpTo(Screen.Resumo.route) { inclusive = true }
        }
    }

    val etapa = when {
        animProgresso < 0.35f -> 0
        animProgresso < 0.68f -> 1
        else -> 2
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(AzulClaro),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Description, null, tint = Azul, modifier = Modifier.size(40.dp))
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Processando...", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Text(
                    "Seu registro está sendo salvo no dispositivo.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                EtapaRow("Salvando dados", etapa >= 0)
                EtapaRow("Salvando imagens", etapa >= 1)
                EtapaRow("Adicionando à fila de envio", etapa >= 2)
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LinearProgressIndicator(
                    progress = { animProgresso },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = Azul,
                    trackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
                Text(
                    if (animProgresso >= 1f) "Concluído" else "Processando...",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun EtapaRow(texto: String, concluido: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(CircleShape)
                .background(if (concluido) Verde else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            if (concluido) {
                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
            }
        }
        Text(
            texto,
            fontSize = 13.sp,
            color = if (concluido) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Composable
fun ConcluidoScreen(
    id: Long,
    navController: NavController,
    vm: RegistroViewModel = hiltViewModel()
) {
    val dataHora = remember { "${DateUtil.dataAtual()}  ${DateUtil.horaAtual()}" }

    Scaffold(
        topBar = {
            com.registrocarregamento.ui.components.AppTopBar(titulo = "Registro Concluído")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Verde),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(40.dp))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Registro salvo com sucesso!", fontSize = 17.sp, fontWeight = FontWeight.Medium)
                Text(
                    "Seu registro foi salvo e será enviado por e-mail quando houver conexão com a internet.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ) {
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.CalendarToday, null, tint = Azul, modifier = Modifier.size(18.dp))
                        Column {
                            Text("Data e hora do registro",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Text(dataHora, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                    HorizontalDivider(thickness = 0.5.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(Icons.Default.Cloud, null, tint = Azul, modifier = Modifier.size(18.dp))
                        Column {
                            Text("Status",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                            Spacer(Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFFFF8E1)
                            ) {
                                Text(
                                    "Pendente de envio",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF6D4C00),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    vm.limparTudo()
                    navController.navigate(Screen.Registro.route) {
                        popUpTo(Screen.Registro.route) { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Azul),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("+ Novo Registro", fontSize = 14.sp)
            }
        }
    }
}
