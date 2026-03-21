package com.tfg_rm.desktopapp_restaurantmanager

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.ExampleViewModel
import com.tfg_rm.desktopapp_restaurantmanager.data.repository.ExampleRepository
import com.tfg_rm.desktopapp_restaurantmanager.data.service.ExampleService
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.MainScreen
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.LoginScreen

@Composable
fun App(viewModel: ExampleViewModel) {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var loggedIn by remember { mutableStateOf(false) }

            if (!loggedIn) {
                LoginScreen(onLogin = { loggedIn = true }, modifier = Modifier.fillMaxSize())
            } else {
                MainScreen(viewModel)
            }
        }
    }
}

@Composable
@Preview
fun AppPreview() {
    val previewVm = remember { ExampleViewModel(ExampleRepository(ExampleService())) }
    App(previewVm)
}