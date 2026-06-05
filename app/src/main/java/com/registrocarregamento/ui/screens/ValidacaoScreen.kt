package com.registrocarregamento.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.registrocarregamento.ui.components.*
import com.registrocarregamento.ui.navigation.Screen

@Composable
fun ValidacaoScreen(
    navController: NavController,
    vm: RegistroViewModel = hiltViewModel()
) {
    val rascunho by vm.rascunho.collectAsState()

    Scaffold(
        topBar = {
            AppTopBar(
                titulo = "Validação",
                subtitulo = "5. Validação",
                onBack = { navController.popBackStack() }
            )
        },
        bottomBar = {
            BotoesPrincipais(
                textoSecundario = "← Voltar",
                textoPrimario = "Próximo →",
                onSecundario = { navController.popBackStack() },
                onPrimario = { navController.navigate(Screen.Resumo.route) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Cidade bate com a CT-e?",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                "Confirme se a cidade informada está de acordo com o CT-e.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            OpcaoRadio(
                texto = "Sim, bate",
                selecionado = rascunho.cidadeConfereCte,
                onClick = { vm.atualizarCidadeConfereCte(true) }
            )

            OpcaoRadio(
                texto = "Não, não bate",
                selecionado = !rascunho.cidadeConfereCte,
                onClick = { vm.atualizarCidadeConfereCte(false) }
            )

            if (!rascunho.cidadeConfereCte) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AmareloBg)
                        .border(0.5.dp, AmarBorda, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(Icons.Default.Warning, null,
                        tint = Color(0xFFF57F17), modifier = Modifier.size(18.dp))
                    Text(
                        "Em caso de divergência, informe o responsável.",
                        fontSize = 12.sp,
                        color = AmareloTexto,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OpcaoRadio(texto: String, selecionado: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (selecionado)
                    Modifier.border(1.dp, Azul, RoundedCornerShape(8.dp))
                        .background(AzulClaro)
                else
                    Modifier.border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        RoundedCornerShape(8.dp))
            )
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .then(
                    if (selecionado) Modifier.background(Azul)
                    else Modifier.border(2.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (selecionado) {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color.White))
            }
        }
        Text(texto, fontSize = 14.sp, fontWeight = if (selecionado) FontWeight.Medium else FontWeight.Normal)
    }
}
