package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.ExampleViewModel
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    navigate: (String) -> Unit
) {

    val viewModel = koinViewModel<ExampleViewModel>()


    val greeting by viewModel.greeting.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = greeting)
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = { viewModel.loadGreeting() }) {
            Text(Strings.t("main.load_button"))
        }
    }
}
