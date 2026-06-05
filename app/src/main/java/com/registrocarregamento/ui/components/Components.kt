package com.registrocarregamento.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

val Azul = Color(0xFF1565C0)
val AzulClaro = Color(0xFFE3F2FD)
val Verde = Color(0xFF2E7D32)
val VerdeClaro = Color(0xFFE8F5E9)
val AmareloBg = Color(0xFFFFF8E1)
val AmareloTexto = Color(0xFF6D4C00)
val AmarBorda = Color(0xFFFFE082)

@Composable
fun AppTopBar(
    titulo: String,
    subtitulo: String? = null,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Surface(color = Azul, shadowElevation = 4.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 4.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }
            } else {
                Spacer(Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                if (subtitulo != null) {
                    Text(subtitulo, color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp,
                        fontWeight = FontWeight.Medium, letterSpacing = 0.8.sp)
                }
                Text(titulo, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Medium)
            }
            actions()
            Spacer(Modifier.width(4.dp))
        }
    }
}

@Composable
fun BadgeOffline(pendentes: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(VerdeClaro)
            .border(0.5.dp, Color(0xFFA5D6A7), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Verde)
        )
        Text(
            text = if (pendentes > 0)
                "Modo Offline — $pendentes registro(s) pendente(s) de envio"
            else
                "Modo Offline — registros enviados quando houver conexão",
            color = Verde, fontSize = 12.sp
        )
    }
}

@Composable
fun DocumentoRow(
    titulo: String,
    obrigatorio: Boolean,
    fotoPath: String?,
    onClick: () -> Unit
) {
    val capturado = fotoPath != null
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (capturado) VerdeClaro else AzulClaro),
            contentAlignment = Alignment.Center
        ) {
            if (capturado) {
                Icon(Icons.Default.CheckCircle, null, tint = Verde, modifier = Modifier.size(22.dp))
            } else {
                Icon(Icons.Default.CameraAlt, null, tint = Azul, modifier = Modifier.size(22.dp))
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Row {
                Text(titulo, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                if (obrigatorio) Text(" *", color = Color(0xFFC62828), fontSize = 12.sp)
            }
            Text(
                text = if (capturado) "Foto capturada ✓" else if (obrigatorio) "Obrigatória" else "Opcional",
                fontSize = 11.sp,
                color = if (capturado) Verde else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        if (capturado && fotoPath != null) {
            AsyncImage(
                model = Uri.parse(fotoPath),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(Icons.Default.ChevronRight, null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
    }
}

@Composable
fun BotoesPrincipais(
    textoSecundario: String = "Limpar tudo",
    textoPrimario: String = "Próximo →",
    onSecundario: () -> Unit,
    onPrimario: () -> Unit,
    habilitadoPrimario: Boolean = true
) {
    Surface(shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = onSecundario,
                modifier = Modifier.weight(1f)
            ) {
                Text(textoSecundario, fontSize = 13.sp)
            }
            Button(
                onClick = onPrimario,
                modifier = Modifier.weight(2f),
                enabled = habilitadoPrimario,
                colors = ButtonDefaults.buttonColors(containerColor = Azul)
            ) {
                Text(textoPrimario, fontSize = 13.sp)
            }
        }
    }
}
