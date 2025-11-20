package com.example.comparacombustvel

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
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
                val context = LocalContext.current

                if (!view.isInEditMode) {
                    LaunchedEffect(Unit) {
                        val window = (view.context as Activity).window
                        window.statusBarColor = colorScheme.primary.toArgb()
                        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                    }
                }

                var showPermissionRationale by remember { mutableStateOf(false) }

                val permissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    val granted = permissions.entries.any { it.value }
                    if (!granted) {
                        showPermissionRationale = true
                    }
                }

                LaunchedEffect(Unit) {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }

                if (showPermissionRationale) {
                    AlertDialog(
                        onDismissRequest = { showPermissionRationale = false },
                        title = { Text("Permissão Necessária") },
                        text = {
                            Text("O acesso à localização foi negado permanentemente. Para usar o recurso de GPS automático, você precisa habilitar manualmente nas configurações.")
                        },
                        confirmButton = {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {

                                Button(
                                    onClick = {
                                        showPermissionRationale = false
                                        val intent = Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", context.packageName, null)
                                        )
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Abrir Configurações")
                                }

                                TextButton(
                                    onClick = {
                                        showPermissionRationale = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Continuar sem permissão")
                                }
                            }
                        }
                    )
                }

                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {

                    composable("home") {
                        Home(
                            onNavigateToRentavel = { navController.navigate("rentavel") },
                            onNavigateToListaPostos = { navController.navigate("listaPostos") }
                        )
                    }

                    composable("rentavel") {
                        Rentavel(
                            navController = navController,
                            postoId = "new",
                        )
                    }

                    composable("listaPostos") {
                        ListaPostos(
                            navController = navController,
                            onNavigateToRentavel = { navController.navigate("rentavel/new") }
                        )
                    }

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