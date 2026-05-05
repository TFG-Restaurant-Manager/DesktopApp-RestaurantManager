package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.EconomyViewModel
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings

@Composable
fun EconomyScreen(viewModel: EconomyViewModel, modifier: Modifier = Modifier) {
    val title = Strings.t("screen.economy.title")
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = Strings.t("placeholder.economy"))
    }
}
