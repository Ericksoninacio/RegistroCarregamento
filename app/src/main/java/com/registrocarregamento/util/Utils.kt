package com.registrocarregamento.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object NetworkUtil {
    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

object FileUtil {
    fun criarDiretorioCarregamento(context: Context, placa: String): File {
        val data = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
        val pasta = File(context.getExternalFilesDir(null), "Carregamentos/${data}_${placa}")
        if (!pasta.exists()) pasta.mkdirs()
        return pasta
    }

    fun criarArquivoFoto(context: Context, placa: String, nome: String): File {
        val dir = criarDiretorioCarregamento(context, placa)
        return File(dir, "$nome.jpg")
    }
}

object DateUtil {
    private val fmtData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val fmtHora = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun dataAtual(): String = fmtData.format(Date())
    fun horaAtual(): String = fmtHora.format(Date())
}
