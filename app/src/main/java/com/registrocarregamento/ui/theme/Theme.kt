package com.registrocarregamento.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Blue700 = Color(0xFF1565C0)
private val Blue50 = Color(0xFFE3F2FD)
private val Blue900 = Color(0xFF0D47A1)

private val ColorScheme = lightColorScheme(
    primary = Blue700,
    onPrimary = Color.White,
    primaryContainer = Blue50,
    onPrimaryContainer = Blue900,
    surface = Color.White,
    background = Color(0xFFF5F5F5),
    error = Color(0xFFC62828),
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = ColorScheme, content = content)
}
