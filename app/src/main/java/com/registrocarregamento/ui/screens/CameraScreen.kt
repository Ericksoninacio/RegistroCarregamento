package com.registrocarregamento.ui.screens

import android.content.Context
import android.net.Uri
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.registrocarregamento.util.FileUtil
import java.io.File
import java.util.concurrent.Executors

@Composable
fun CameraScreen(
    titulo: String,
    nomeFoto: String,
    placa: String = "temp",
    detectarPlaca: Boolean = false,
    onFotoCapturada: (String, String?) -> Unit,
    onFechar: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var camera by remember { mutableStateOf<Camera?>(null) }
    var capturando by remember { mutableStateOf(false) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_AUTO) }
    val executor = remember { Executors.newSingleThreadExecutor() }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setFlashMode(ImageCapture.FLASH_MODE_AUTO) // flash automático
                        .build()
                    imageCapture = capture
                    cameraProvider.unbindAll()
                    val cam = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        capture
                    )
                    camera = cam
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Guia de enquadramento
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.85f)
                .aspectRatio(if (detectarPlaca) 3f else 1.4f)
                .border(2.dp, Color.White.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
        )

        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onFechar) {
                Icon(Icons.Default.Close, "Fechar", tint = Color.White)
            }
            Text(
                titulo,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            // Botão flash
            IconButton(onClick = {
                flashMode = when (flashMode) {
                    ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
                    ImageCapture.FLASH_MODE_ON   -> ImageCapture.FLASH_MODE_OFF
                    else                          -> ImageCapture.FLASH_MODE_AUTO
                }
                imageCapture?.flashMode = flashMode
            }) {
                Icon(
                    imageVector = when (flashMode) {
                        ImageCapture.FLASH_MODE_ON   -> Icons.Default.FlashOn
                        ImageCapture.FLASH_MODE_OFF  -> Icons.Default.FlashOff
                        else                          -> Icons.Default.FlashAuto
                    },
                    contentDescription = "Flash",
                    tint = when (flashMode) {
                        ImageCapture.FLASH_MODE_ON -> Color.Yellow
                        else                        -> Color.White
                    }
                )
            }
        }

        // Dica
        Text(
            text = "Enquadre dentro do retângulo",
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = if (detectarPlaca) 60.dp else 100.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )

        // Botão captura
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 40.dp)
        ) {
            if (capturando) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(60.dp))
            } else {
                IconButton(
                    onClick = {
                        capturando = true
                        capturarFoto(
                            context = context,
                            imageCapture = imageCapture,
                            placa = placa,
                            nomeFoto = nomeFoto,
                            executor = executor,
                            detectarPlaca = detectarPlaca,
                            onResultado = { path, placaDetectada ->
                                capturando = false
                                onFotoCapturada(path, placaDetectada)
                            },
                            onErro = { capturando = false }
                        )
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        Icons.Default.PhotoCamera,
                        "Capturar",
                        tint = Color(0xFF1565C0),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

private fun capturarFoto(
    context: Context,
    imageCapture: ImageCapture?,
    placa: String,
    nomeFoto: String,
    executor: java.util.concurrent.Executor,
    detectarPlaca: Boolean,
    onResultado: (String, String?) -> Unit,
    onErro: () -> Unit
) {
    val ic = imageCapture ?: run { onErro(); return }
    val arquivo = FileUtil.criarArquivoFoto(context, placa.ifBlank { "temp" }, nomeFoto)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(arquivo).build()

    ic.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
            val path = arquivo.absolutePath
            if (detectarPlaca) {
                reconhecerPlacaDeArquivo(context, arquivo) { placaDetectada ->
                    onResultado(path, placaDetectada)
                }
            } else {
                onResultado(path, null)
            }
        }
        override fun onError(exc: ImageCaptureException) { onErro() }
    })
}

fun reconhecerPlacaDeArquivo(context: Context, arquivo: File, onPlaca: (String?) -> Unit) {
    try {
        val image = InputImage.fromFilePath(context, Uri.fromFile(arquivo))
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val placaRegex = Regex("[A-Z]{3}[0-9][A-Z0-9][0-9]{2}")
                val placa = visionText.textBlocks
                    .flatMap { it.lines }
                    .map { it.text.replace(" ", "").uppercase() }
                    .firstOrNull { placaRegex.containsMatchIn(it) }
                    ?.let { placaRegex.find(it)?.value }
                onPlaca(placa)
            }
            .addOnFailureListener { onPlaca(null) }
    } catch (e: Exception) {
        onPlaca(null)
    }
}