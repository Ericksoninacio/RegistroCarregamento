package com.registrocarregamento.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
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
    val registroViewModel: RegistroViewModel = hiltViewModel() // ← adicionar aqui

    NavHost(navController = navController, startDestination = Screen.Registro.route) {
        composable(Screen.Registro.route) {
            RegistroScreen(navController = navController, vm = registroViewModel) // ← vm = ...
        }
        composable(Screen.Documentos.route) {
            DocumentosScreen(navController = navController, vm = registroViewModel) // ← vm = ...
        }
        composable(Screen.Validacao.route) {
            ValidacaoScreen(navController = navController, vm = registroViewModel) // ← vm = ...
        }
        composable(Screen.Resumo.route) {
            ResumoScreen(navController = navController, vm = registroViewModel) // ← vm = ...
        }
        composable(Screen.Processando.route) {
            ProcessandoScreen(navController = navController, vm = registroViewModel) // ← vm = ...
        }
        composable(Screen.Concluido.route) { backStack ->
            val id = backStack.arguments?.getString("id")?.toLongOrNull() ?: 0L
            ConcluidoScreen(id = id, navController = navController, vm = registroViewModel) // ← adicionar vm
        }
        composable(Screen.Historico.route) {
            HistoricoScreen(navController = navController) // ← sem vm (usa seu próprio)
        }
        composable(Screen.Configuracoes.route) {
            ConfiguracoesScreen(navController = navController) // ← sem vm
        }
    }
}
