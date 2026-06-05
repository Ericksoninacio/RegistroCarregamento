package com.registrocarregamento.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.registrocarregamento.ui.screens.*

sealed class Screen(val route: String) {
    object Registro : Screen("registro")
    object Documentos : Screen("documentos")
    object Validacao : Screen("validacao")
    object Resumo : Screen("resumo")
    object Processando : Screen("processando")
    object Concluido : Screen("concluido/{id}") {
        fun withId(id: Long) = "concluido/$id"
    }
    object Historico : Screen("historico")
    object Configuracoes : Screen("configuracoes")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Registro.route) {
        composable(Screen.Registro.route) {
            RegistroScreen(navController = navController)
        }
        composable(Screen.Documentos.route) {
            DocumentosScreen(navController = navController)
        }
        composable(Screen.Validacao.route) {
            ValidacaoScreen(navController = navController)
        }
        composable(Screen.Resumo.route) {
            ResumoScreen(navController = navController)
        }
        composable(Screen.Processando.route) {
            ProcessandoScreen(navController = navController)
        }
        composable(Screen.Concluido.route) { backStack ->
            val id = backStack.arguments?.getString("id")?.toLongOrNull() ?: 0L
            ConcluidoScreen(id = id, navController = navController)
        }
        composable(Screen.Historico.route) {
            HistoricoScreen(navController = navController)
        }
        composable(Screen.Configuracoes.route) {
            ConfiguracoesScreen(navController = navController)
        }
    }
}
