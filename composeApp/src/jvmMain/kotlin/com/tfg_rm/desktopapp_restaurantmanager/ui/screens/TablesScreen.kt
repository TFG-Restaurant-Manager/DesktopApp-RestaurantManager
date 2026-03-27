package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.TablesViewModel
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings

@Composable
fun TablesScreen(viewModel: TablesViewModel, modifier: Modifier = Modifier) {
    val title by viewModel.title.collectAsState()
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = title)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = Strings.t("placeholder.tables"))
    }
}
