package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Order
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.OrdersViewModel
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.components.OrderItemView
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.components.UiState
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings


@Composable
fun OrdersScreen(viewModel: OrdersViewModel, modifier: Modifier = Modifier) {
    val title by viewModel.title.collectAsState()
    val state by viewModel.orders.collectAsState()

    when (state) {
        is UiState.Error -> {
            ErrorScreen(
                title = Strings.t("screen.orders.error.generic"),
                message = (state as UiState.Error).message,
                primaryAction = Pair(Strings.t("reload"), { viewModel.loadOrders() })
            )
        }

        UiState.Idle -> {
            viewModel.loadOrders()
        }

        UiState.Loading -> {
            LoadingScreen(
                text = Strings.t("screen.orders.loading.message")
            )
        }

        is UiState.Success<*> -> {
            val orders = (state as UiState.Success<List<Order>>).data

            Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
                Text(text = title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))

                // Top indicators row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IndicatorCard(
                        modifier = Modifier.weight(1f),
                        label = Strings.t("screen.orders.pendingorders"),
                        value = orders.size.toString(),
                        color = Color(0xFFFF7043)
                    )
                    IndicatorCard(
                        modifier = Modifier.weight(1f),
                        label = Strings.t("screen.orders.averagetime"),
                        value = viewModel.averageTimeLabel(orders),
                        color = Color(0xFF4CAF50)
                    )
                    IndicatorCard(
                        modifier = Modifier.weight(1f),
                        label = Strings.t("screen.orders.activetables"),
                        value = orders.map { it.tableId }.distinct().size.toString(),
                        color = Color(0xFF2196F3)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Alert if any order > 20 min
                if (orders.any { viewModel.elapsedMinutes(it) > 20 }) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Spacer(modifier = Modifier.size(8.dp))
                            Column {
                                Text(text = Strings.t("screen.orders.delayedorders"), fontWeight = FontWeight.SemiBold)
                                Text(
                                    text = Strings.t("screen.orders.delayedorders.subtext"),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Orders grid — one card per item unit, numbered sequentially 1, 2, 3…
                val flatItems = viewModel.buildFlatEntries(orders)
                LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 260.dp), modifier = Modifier.fillMaxHeight()) {
                    items(flatItems) { entry ->
                        OrderItemView(
                            item = entry.item.copy(quantity = 1),
                            tableId = entry.order.tableId,
                            orderId = entry.order.id,
                            displayNumber = entry.displayNumber,
                            createdAt = entry.order.createdAt,
                            onComplete = { viewModel.completeOrderItem(entry.order.id, entry.item.id) }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun IndicatorCard(modifier: Modifier = Modifier, label: String, value: String, color: Color) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}



