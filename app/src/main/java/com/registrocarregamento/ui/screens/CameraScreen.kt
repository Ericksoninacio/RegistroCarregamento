package com.registrocarregamento.ui.screens

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import kotlinx.coroutines.delay
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalGetImage::class)
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

    // Estado de detecção automática
    var placaDetectadaPreview by remember { mutableStateOf<String?>(null) }
    var bordaAtiva by remember { mutableStateOf(false) }

    val executor = remember { Executors.newSingleThreadExecutor() }

    // Controle de throttle: analisa 1 frame por segundo no máximo
    val ultimaAnalise = remember { AtomicLong(0L) }
    // Flag para evitar captura dupla
    val capturandoAuto = remember { AtomicBoolean(false) }

    // Borda animada: branca normal, verde ao detectar placa
    val corBorda by animateColorAsState(
        targetValue = if (bordaAtiva) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.7f),
        animationSpec = tween(300),
        label = "bordaDeteccao"
    )

    // Ao detectar placa no preview, pisca a borda e captura automaticamente
    LaunchedEffect(placaDetectadaPreview) {
        val placaEncontrada = placaDetectadaPreview ?: return@LaunchedEffect
        if (capturando) return@LaunchedEffect

        bordaAtiva = true
        delay(200.milliseconds) // feedback visual antes de capturar
        bordaAtiva = false

        if (!capturandoAuto.getAndSet(true)) {
            capturando = true
            capturarFoto(
                context = context,
                imageCapture = imageCapture,
                placa = placa,
                nomeFoto = nomeFoto,
                executor = executor,
                detectarPlaca = true,
                onResultado = { path, detectedPlaca ->
                    capturando = false
                    capturandoAuto.set(false)
                    // Prefere a placa confirmada na foto; fallback para a do preview
                    onFotoCapturada(path, detectedPlaca ?: placaEncontrada)
                },
                onErro = {
                    capturando = false
                    capturandoAuto.set(false)
                }
            )
        }
    }

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
                        .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
                        .build()
                    imageCapture = capture

                    // ImageAnalysis para detecção automática de placa
                    val useCases = mutableListOf<UseCase>(preview, capture)

                    if (detectarPlaca) {
                        val analysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
                        val placaRegex = Regex("[A-Z]{3}[0-9][A-Z0-9][0-9]{2}")

                        analysis.setAnalyzer(executor) { imageProxy ->
                            val agora = System.currentTimeMillis()
                            // Throttle: ignora frames se o último foi há menos de 1s
                            if (agora - ultimaAnalise.get() < 1000L || capturandoAuto.get()) {
                                imageProxy.close()
                                return@setAnalyzer
                            }
                            ultimaAnalise.set(agora)

                            val mediaImage = imageProxy.image
                            if (mediaImage != null) {
                                val inputImage = InputImage.fromMediaImage(
                                    mediaImage,
                                    imageProxy.imageInfo.rotationDegrees
                                )
                                recognizer.process(inputImage)
                                    .addOnSuccessListener { visionText ->
                                        val encontrada = visionText.textBlocks
                                            .flatMap { it.lines }
                                            .map { it.text.replace(" ", "").uppercase() }
                                            .firstOrNull { placaRegex.containsMatchIn(it) }
                                            ?.let { placaRegex.find(it)?.value }

                                        if (encontrada != null) {
                                            placaDetectadaPreview = encontrada
                                        }
                                    }
                                    .addOnCompleteListener { imageProxy.close() }
                            } else {
                                imageProxy.close()
                            }
                        }
                        useCases.add(analysis)
                    }

                    cameraProvider.unbindAll()
                    val cam = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        *useCases.toTypedArray()
                    )
                    camera = cam

                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Guia de enquadramento com borda animada
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.85f)
                .aspectRatio(if (detectarPlaca) 3f else 1.4f)
                .border(2.dp, corBorda, RoundedCornerShape(8.dp))
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
                        ImageCapture.FLASH_MODE_ON  -> Icons.Default.FlashOn
                        ImageCapture.FLASH_MODE_OFF -> Icons.Default.FlashOff
                        else                         -> Icons.Default.FlashAuto
                    },
                    contentDescription = "Flash",
                    tint = if (flashMode == ImageCapture.FLASH_MODE_ON) Color.Yellow else Color.White
                )
            }
        }

        // Dica / status de detecção
        val textoStatus = if (detectarPlaca && placaDetectadaPreview != null && !capturando)
            "Placa detectada: ${placaDetectadaPreview}  ✓"
        else if (detectarPlaca)
            "Aponte para a placa do veículo"
        else
            "Enquadre dentro do retângulo"

        Text(
            text = textoStatus,
            color = if (placaDetectadaPreview != null) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            fontWeight = if (placaDetectadaPreview != null) FontWeight.Medium else FontWeight.Normal,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = if (detectarPlaca) 60.dp else 100.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        )

        // Botão captura manual (fallback)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 40.dp)
        ) {
            if (capturando) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(60.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Capturando...", color = Color.White, fontSize = 12.sp)
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                                onResultado = { path, placaDetected ->
                                    capturando = false
                                    onFotoCapturada(path, placaDetected)
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
                    if (detectarPlaca) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Captura manual",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }
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