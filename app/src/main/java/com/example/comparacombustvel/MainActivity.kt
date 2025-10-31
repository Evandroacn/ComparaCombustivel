package com.example.comparacombustvel

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.comparacombustvel.ui.theme.ComparaCombustivelTheme
import com.example.comparacombustvel.view.DetalhesPosto
import com.example.comparacombustvel.view.Home
import com.example.comparacombustvel.view.ListaPostos
import com.example.comparacombustvel.view.Rentavel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComparaCombustivelTheme {
                val colorScheme = MaterialTheme.colorScheme
                val view = LocalView.current
                if (!view.isInEditMode) {
                    LaunchedEffect(Unit) {
                        val window = (view.context as Activity).window
                        window.statusBarColor = colorScheme.primary.toArgb()
                        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                    }
                }

                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {

                    composable("home") {
                        Home(
                            onNavigateToRentavel = { navController.navigate("rentavel") },
                            onNavigateToListaPostos = { navController.navigate("listaPostos") }
                        )
                    }

                    // Rota para Rentável (verificar rentabilidade e adicionar posto)
                    composable("rentavel") {
                        Rentavel(
                            navController = navController,
                            postoId = "new",
                        )
                    }

                    // Rota para a Lista de Postos
                    composable("listaPostos") {
                        ListaPostos(
                            navController = navController,
                            onNavigateToRentavel = { navController.navigate("rentavel/new") }
                        )
                    }

                    // Rota para Editar Posto
                    composable(
                        route = "rentavel/{postoId}",
                        arguments = listOf(navArgument("postoId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val postoId = backStackEntry.arguments?.getString("postoId")
                        Rentavel(
                            navController = navController,
                            postoId = postoId,
                        )
                    }

                    // Rota para os Detalhes do Posto
                    composable(
                        route = "detalhesPosto/{postoId}",
                        arguments = listOf(navArgument("postoId") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val postoId = backStackEntry.arguments?.getString("postoId")
                        if (postoId != null) {
                            DetalhesPosto(
                                navController = navController,
                                postoId = postoId
                            )
                        }
                    }
                }
            }
        }
    }
}