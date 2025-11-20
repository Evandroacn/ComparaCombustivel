package com.example.comparacombustvel.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.navigation.NavController
import com.example.comparacombustvel.datasource.Calculations
import com.example.comparacombustvel.datasource.PostoManager
import com.example.comparacombustvel.datasource.PostoRepository
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import androidx.compose.ui.res.stringResource
import com.example.comparacombustvel.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Rentavel(
    navController: NavController,
    postoId: String?,
) {
    val context = LocalContext.current
    val repository = remember { PostoRepository(context) }
    val isEditing = postoId != null && postoId != "new"

    // --- Cliente de Localização ---
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // --- Estados da UI ---
    var alcool by rememberSaveable { mutableStateOf("") }
    var gasolina by rememberSaveable { mutableStateOf("") }
    var posto by rememberSaveable { mutableStateOf("") }

    // --- Localização ---
    var localizacao by rememberSaveable { mutableStateOf("") }

    var textResultCalculo by rememberSaveable { mutableStateOf("") }
    var showRentabilidade by rememberSaveable { mutableStateOf(false) }

    // --- Estados de Erro ---
    var alcoolError by rememberSaveable { mutableStateOf<String?>(null) }
    var gasolinaError by rememberSaveable { mutableStateOf<String?>(null) }
    var postoError by rememberSaveable { mutableStateOf<String?>(null) }

    // --- Estado do Switch ---
    val key_switch_state = "estado_do_switch_rentavel"
    val sharedPreferences = remember { context.getSharedPreferences("app_preferencias", Context.MODE_PRIVATE) }
    var checked by rememberSaveable {
        mutableStateOf(sharedPreferences.getBoolean(key_switch_state, true))
    }

    // --- Função para capturar GPS ---
    fun captureLocationSilent() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {
            try {
                fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
                    .addOnSuccessListener { location: Location? ->
                        if (location != null) {
                            localizacao = "${location.latitude}, ${location.longitude}"
                        }
                    }
            } catch (e: Exception) {

            }
        }
    }

    fun verificacaoAlcoolGasolina(): Boolean {
        val alcoolVal = alcool.replace(",", ".").toDoubleOrNull()
        val gasolinaVal = gasolina.replace(",", ".").toDoubleOrNull()
        var hasError = false

        // Valida Alcool
        if (alcool.isBlank()) {
            alcoolError = context.getString(R.string.error_required_field)
            hasError = true
        } else if (alcoolVal == null) {
            alcoolError = context.getString(R.string.error_invalid_number)
            hasError = true
        } else {
            alcoolError = null
        }

        // Valida Gasolina
        if (gasolina.isBlank()) {
            gasolinaError = context.getString(R.string.error_required_field)
            hasError = true
        } else if (gasolinaVal == null) {
            gasolinaError = context.getString(R.string.error_invalid_number)
            hasError = true
        } else {
            gasolinaError = null
        }

        return !hasError
    }

    // --- Lógica de Inicialização ---
    LaunchedEffect(postoId) {
        if (isEditing) {
            val existingPosto = repository.getPostoById(postoId!!)
            if (existingPosto != null) {
                alcool = existingPosto.precoAlcool
                gasolina = existingPosto.precoGasolina
                posto = existingPosto.nome
                localizacao = existingPosto.localizacao ?: ""
                checked = existingPosto.porcentagemCalculo == 75
            }
        } else {
            captureLocationSilent()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (isEditing) stringResource(R.string.title_edit_station) else stringResource(R.string.title_add_station))
                },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = alcool,
                onValueChange = { if (it.length <= 4) alcool = it },
                label = { Text(stringResource(R.string.label_alcohol_price)) },
                modifier = Modifier.fillMaxWidth().padding(20.dp, 30.dp, 20.dp, 5.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = alcoolError != null
            )

            if (alcoolError != null) {
                Text(
                    text = alcoolError!!,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth().padding(20.dp, 0.dp)
                )
            }

            OutlinedTextField(
                value = gasolina,
                onValueChange = { if (it.length <= 4) gasolina = it },
                label = { Text(text = stringResource(R.string.label_gasoline_price)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 5.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = gasolinaError != null
            )

            if (gasolinaError != null) {
                Text(
                    text = gasolinaError!!,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth().padding(20.dp, 0.dp)
                )
            }

            OutlinedTextField(
                value = posto,
                onValueChange = { posto = it },
                label = { Text(text = stringResource(R.string.label_station_name)) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                isError = postoError != null
            )

            if (postoError != null) {
                Text(
                    text = postoError!!,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth().padding(20.dp, 0.dp)
                )
            }

            Text(
                text = stringResource(R.string.label_percentage_info),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
                modifier = Modifier.padding(20.dp, 10.dp, 20.dp, 3.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(20.dp, 0.dp, 20.dp, 10.dp)
            ) {
                Switch(
                    checked = checked,
                    onCheckedChange = { novoValor ->
                        checked = novoValor
                        sharedPreferences.edit {
                            putBoolean(key_switch_state, novoValor)
                        }
                    },
                    thumbContent = if (checked) {
                        {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    } else { null }
                )
                Text(
                    text = if (checked) "75%" else "70%",
                    color = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Button(
                onClick = {
                    postoError = null
                    val isFormValid = verificacaoAlcoolGasolina()

                    if (isFormValid) {
                        val alcoolVal = alcool.replace(",", ".").toDouble()
                        val gasolinaVal = gasolina.replace(",", ".").toDouble()

                        val result = Calculations.calculate(
                            context = context,
                            alcool = alcoolVal,
                            gasolina = gasolinaVal,
                            posto = posto,
                            porcentagem = checked
                        )

                        textResultCalculo = result
                        showRentabilidade = true
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp).height(70.dp)
            ) {
                Text(
                    text = stringResource(R.string.btn_calculate),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            OutlinedButton(
                onClick = {
                    textResultCalculo = ""

                    // 1. Valida Álcool e Gasolina usando a função
                    val isAlcoolGasolinaValid = verificacaoAlcoolGasolina()

                    // 2. Valida o Posto
                    var isPostoValid = true
                    if (posto.isBlank()) {
                        postoError = context.getString(R.string.error_required_field)
                        isPostoValid = false
                    } else {
                        postoError = null
                    }

                    if (isAlcoolGasolinaValid && isPostoValid) {
                        PostoManager.savePosto(
                            context = context,
                            alcool = alcool,
                            gasolina = gasolina,
                            posto = posto,
                            localizacao = localizacao,
                            checked = checked,
                            isEditing = isEditing,
                            postoId = postoId,
                            repository = repository,
                            onResult = { success, message ->
                                if (success) {
                                    alcoolError = null
                                    gasolinaError = null
                                    postoError = null
                                    navController.popBackStack()
                                } else {
                                    textResultCalculo = message
                                }
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp).height(70.dp)
            ) {
                Text(
                    text = if (isEditing) stringResource(R.string.btn_save_changes) else stringResource(R.string.btn_add_station),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (showRentabilidade) {
                AlertDialog(
                    onDismissRequest = { showRentabilidade = false },
                    title = { Text(stringResource(R.string.dialog_result_title)) },
                    text = { Text(textResultCalculo) },
                    confirmButton = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Button(
                                onClick = { showRentabilidade = false },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(stringResource(R.string.btn_ok))
                            }
                        }
                    },
                    dismissButton = {}
                )
            }
        }
    }
}