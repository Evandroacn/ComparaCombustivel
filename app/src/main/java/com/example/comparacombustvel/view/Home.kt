package com.example.comparacombustvel.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.res.stringResource
import com.example.comparacombustvel.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    onNavigateToRentavel: () -> Unit,
    onNavigateToListaPostos: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.home_title)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(paddingValues).background(color = MaterialTheme.colorScheme.background)
        ) {

            Image(
                painter = painterResource(id = R.drawable.falloutguy),
                contentDescription = stringResource(R.string.desc_image_home),
                modifier = Modifier.size(200.dp)
            )

            Text(
                text = stringResource(R.string.welcome_message),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 32.sp,
                lineHeight = 40.sp,
                modifier = Modifier.padding(20.dp, 0.dp, 20.dp, 20.dp),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = { onNavigateToRentavel() },
                modifier = Modifier.fillMaxWidth().padding(20.dp, 20.dp, 20.dp, 10.dp).height(70.dp)
            ) {
                Text(
                    text = stringResource(R.string.btn_check_profitability),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = { onNavigateToListaPostos() },
                modifier = Modifier.fillMaxWidth().padding(20.dp, 10.dp, 20.dp, 20.dp).height(70.dp),
            ) {
                Text(
                    text = stringResource(R.string.btn_view_saved),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}