package com.example.comparacombustvel.view

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.comparacombustvel.datasource.Calculations
import com.example.comparacombustvel.datasource.PostoRepository
import com.example.comparacombustvel.model.Posto
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesPosto(
    navController: NavController,
    postoId: String?
) {
    val context = LocalContext.current
    val repository = remember { PostoRepository(context) }
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val postoState = produceState<Posto?>(initialValue = null, postoId) {
        value = postoId?.let { repository.getPostoById(it) }
    }
    val posto = postoState.value
    var rentabilidadeTexto by rememberSaveable { mutableStateOf("Calculando...") }

    LaunchedEffect(posto) {
        if (posto != null) {
            val alcoolVal = posto.precoAlcool.replace(",", ".").toDoubleOrNull()
            val gasolinaVal = posto.precoGasolina.replace(",", ".").toDoubleOrNull()

            if (alcoolVal != null && gasolinaVal != null) {
                rentabilidadeTexto = Calculations.calculate(
                    alcool = alcoolVal,
                    gasolina = gasolinaVal,
                    posto = posto.nome,
                    porcentagem = posto.porcentagemCalculo == 75
                )
            } else {
                rentabilidadeTexto = "Erro: Valores salvos são inválidos."
            }
        } else if (postoId != null) {
            rentabilidadeTexto = "Erro ao carregar dados."
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(posto?.nome ?: "Detalhes do Posto") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        if (posto == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                if (postoId == null) {
                    Text("Erro: ID do posto não fornecido.")
                } else {
                    CircularProgressIndicator()
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(20.dp).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text("Nome: ${posto.nome}", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Divider()
                Text("Álcool: R$ ${posto.precoAlcool}", fontSize = 18.sp)
                Text("Gasolina: R$ ${posto.precoGasolina}", fontSize = 18.sp)

                Text("Cadastrado em: ${posto.dataFormatada()}", fontSize = 16.sp)
                Text("Percentual Usado: ${posto.porcentagemCalculo}%", fontSize = 16.sp)
                Text("Rentabilidade: $rentabilidadeTexto", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)

                Spacer(modifier = Modifier.height(10.dp))

                val temLocalizacao = !posto.localizacao.isNullOrBlank()

                Button(
                    onClick = {
                        if (temLocalizacao) {
                            try {
                                val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(posto.localizacao)}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                context.startActivity(mapIntent)
                            } catch (e: Exception) {
                                val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(posto.localizacao)}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                context.startActivity(mapIntent)
                            }
                        }
                    },
                    enabled = temLocalizacao,
                    modifier = Modifier.fillMaxWidth().padding(0.dp, 10.dp, 0.dp, 5.dp).height(70.dp)
                ) {
                    Text(
                        if (temLocalizacao) "Abrir Mapa" else "Mapa Indisponível",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                OutlinedButton(
                    onClick = {
                        navController.navigate("rentavel/${posto.id}")
                    },
                    modifier = Modifier.fillMaxWidth().padding(0.dp, 5.dp).height(70.dp)
                ) {
                    Text(
                        "Editar",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Button(
                    onClick = { showDeleteDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth().padding(0.dp, 5.dp).height(70.dp)
                ) {
                    Text(
                        "Excluir",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Confirmar Exclusão") },
                    text = { Text("Tem certeza que deseja excluir o posto '${posto?.nome}'?") },
                    confirmButton = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Button(
                                onClick = { showDeleteDialog = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar")
                            }

                            Button(
                                onClick = {
                                    scope.launch {
                                        repository.deletePosto(posto!!.id)
                                        showDeleteDialog = false
                                        navController.popBackStack()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Excluir")
                            }
                        }
                    },
                    dismissButton = {}
                )
            }
        }
    }
}