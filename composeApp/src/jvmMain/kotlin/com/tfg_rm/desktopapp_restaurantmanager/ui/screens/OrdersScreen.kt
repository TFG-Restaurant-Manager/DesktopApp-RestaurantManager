package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import java.time.Duration
import java.time.LocalDateTime


@Composable
fun OrdersScreen(viewModel: OrdersViewModel, modifier: Modifier = Modifier) {
    val title by viewModel.title.collectAsState()
    val orders by viewModel.orders.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadOrders()
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        // Top indicators row
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IndicatorCard(modifier = Modifier.weight(1f), label = "Orders Pendientes", value = orders.size.toString(), color = Color(0xFFFF7043))
            IndicatorCard(modifier = Modifier.weight(1f), label = "Tiempo Promedio", value = averageTimeLabel(orders), color = Color(0xFF4CAF50))
            IndicatorCard(modifier = Modifier.weight(1f), label = "Mesas Activas", value = orders.map { it.tableId }.distinct().size.toString(), color = Color(0xFF2196F3))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Alert if any order > 20 min
        if (orders.any { elapsedMinutes(it) > 20 }) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)), modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Spacer(modifier = Modifier.size(8.dp))
                    Column {
                        Text(text = "¡Atención! Orders con demora", fontWeight = FontWeight.SemiBold)
                        Text(text = "Hay orders que llevan más de 20 minutos esperando", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // Orders grid — one card per item unit
        val flatItems = orders.flatMap { order ->
            order.orderItemsList.flatMap { item ->
                List(item.quantity) { order to item }
            }
        }
        LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 260.dp), modifier = Modifier.fillMaxHeight()) {
            items(flatItems) { (order, item) ->
                OrderItemView(
                    item = item.copy(quantity = 1),
                    tableId = order.tableId,
                    orderId = order.id,
                    createdAt = order.createdAt,
                    onComplete = { viewModel.completeOrderItem(order.id, item.id) }
                )
            }
        }
    }
}


@Composable
private fun IndicatorCard(modifier: Modifier = Modifier, label: String, value: String, color: Color) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall, color = color, fontWeight = FontWeight.Bold)
        }
    }
}

private fun elapsedMinutes(order: Order): Long {
    return try {
        Duration.between(order.createdAt, LocalDateTime.now()).toMinutes()
    } catch (e: Exception) { 0 }
}

private fun elapsedSecondsPart(order: Order): Long {
    return try {
        val d = Duration.between(order.createdAt, LocalDateTime.now())
        d.seconds % 60
    } catch (e: Exception) { 0 }
}

private fun averageTimeLabel(orders: List<Order>): String {
    if (orders.isEmpty()) return "0 min"
    val avg = orders.map { elapsedMinutes(it) }.average().toInt()
    return "$avg min"
}

