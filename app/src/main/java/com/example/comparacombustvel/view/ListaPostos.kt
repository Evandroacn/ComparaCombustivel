package com.example.comparacombustvel.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.comparacombustvel.datasource.PostoRepository
import com.example.comparacombustvel.model.Posto
import androidx.compose.ui.res.stringResource
import com.example.comparacombustvel.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaPostos(
    navController: NavController,
    onNavigateToRentavel: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { PostoRepository(context) }

    // Estado para guardar a lista de postos (lê a lista inicial)
    val postosList by produceState<List<Posto>>(initialValue = emptyList(), repository) {
        value = repository.getAllPostos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_saved_stations)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.btn_back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToRentavel,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, stringResource(R.string.title_add_station))
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (postosList.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.empty_list_msg),
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                items(postosList, key = { posto -> posto.id }) { posto ->
                    PostoCard(posto = posto, onClick = {
                        navController.navigate("detalhesPosto/${posto.id}")
                    })
                }
            }
        }
    }
}

@Composable
fun PostoCard(posto: Posto, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(posto.nome, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(stringResource(R.string.detail_alcohol, posto.precoAlcool))
            Text(stringResource(R.string.detail_gasoline, posto.precoGasolina))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                stringResource(R.string.detail_date, posto.dataFormatada()),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}