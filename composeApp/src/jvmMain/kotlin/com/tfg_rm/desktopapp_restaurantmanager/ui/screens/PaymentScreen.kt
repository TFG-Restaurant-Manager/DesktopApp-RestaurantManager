package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Order
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.OrdersViewModel
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.components.UiState
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings


@Composable
fun PaymentScreen(viewModel: OrdersViewModel, modifier: Modifier = Modifier) {
    val title = Strings.t("screen.payment.title")
    val state by viewModel.orders.collectAsState()
    var expandedOrderId by remember { mutableStateOf<Int?>(null) }

    when (state) {
        is UiState.Error -> {
            ErrorScreen(
                title = Strings.t("screen.orders.error.generic"),
                message = (state as UiState.Error).message,
                primaryAction = Pair(Strings.t("reload")) { viewModel.loadOrders() }
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
                .filter { it.status == "DELIVERED" }

            LaunchedEffect(orders) {
                if (expandedOrderId != null &&
                    orders.none { it.id == expandedOrderId }
                ) {
                    expandedOrderId = null
                }
            }

            Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
                Text(text = title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    IndicatorCard(
                        modifier = Modifier.weight(1f),
                        label = Strings.t("screen.payment.orderstopay"),
                        value = orders.size.toString(),
                        color = Color(0xFFFF7043)
                    )
                    IndicatorCard(
                        modifier = Modifier.weight(1f),
                        label = Strings.t("screen.payment.activetables"),
                        value = orders.map { it.tableId }.distinct().size.toString(),
                        color = Color(0xFF2196F3)
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = orders,
                        key = { it.id },
                    ) { entry ->
                        OrderItemPaymentView(
                            modifier = Modifier,
                            item = entry,
                            isExpanded = expandedOrderId == entry.id,
                            onExpandClick = {
                                expandedOrderId = if (expandedOrderId == entry.id) null else entry.id
                            },
                            onPay = { order ->
                                viewModel.payOrder(order)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderItemPaymentView(
    item: Order,
    isExpanded: Boolean,
    onExpandClick: () -> Unit,
    onPay: (Order) -> Unit = {},
    modifier: Modifier = Modifier // Añadimos el modifier que viene de la Grid
) {
    val cardShape = RoundedCornerShape(12.dp)

    Card(
        modifier = modifier // Usamos el modifier que incluye animateItem()
            .fillMaxWidth()
            .padding(4.dp)
            .then(
                // Forzamos altura fija en colapsado y flexible en expandido
                if (!isExpanded) Modifier.height(200.dp)
                else Modifier.wrapContentHeight()
            )
            .clip(cardShape)
            .clickable { onExpandClick() },
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = if (isExpanded) Color.White else Color(0xFFF8FAFC)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 6.dp else 2.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (isExpanded) Color(0xFFFF7043) else Color(0xFFE2E8F0)
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // CABECERA (Se mantiene igual)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (item.type) {
                        "TABLE" -> "${Strings.t("screen.orderHistory.col.table")} ${
                            if (item.tableName != null) {
                                if (item.tableName.isEmpty()) item.tableId.toString()
                                else if (item.tableName.length >= 3) item.tableName.substring(
                                    3
                                )
                                else item.tableName
                            } else if (item.tableId == null)
                                Strings.t("screen.newOrden.notableid") else item.tableId.toString()
                        }"

                        "DELIVERY" -> Strings.t("screen.orderHistory.col.delivery")
                        "PICKUP" -> Strings.t("screen.orderHistory.col.pickup")
                        else -> "---"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${item.orderItemsList.sumOf { it.unitPrice * it.quantity }}€",
                    style = if (isExpanded) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isExpanded) Color(0xFFFF7043) else Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // LISTA DE PLATOS
            Column(
                modifier = if (!isExpanded) Modifier.weight(1f) else Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val itemsToShow = if (isExpanded) item.orderItemsList else item.orderItemsList.take(3)

                itemsToShow.forEach { orderItem ->
                    Text(
                        text = "• ${orderItem.quantity}x ${orderItem.dishName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF334155),
                        maxLines = 1
                    )
                }

                if (!isExpanded && item.orderItemsList.size > 3) {
                    Text(
                        text = "+ ${item.orderItemsList.size - 3} ${Strings.t("more")}...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFFF7043),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (isExpanded) {
                // DETALLES ADICIONALES (Se muestran solo al expandir)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE2E8F0))

                Row(modifier = Modifier.fillMaxWidth()) {
                    if (item.notes?.isNotEmpty() == true) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                Strings.t("notes"),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                            Text(item.notes, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    if (item.orderType == "DELIVERY") {
                        InfoRow(Icons.Default.LocationOn, item.deliveryAddress ?: "---")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { onPay(item) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(Strings.t("screen.payment.pay_button"), fontWeight = FontWeight.Bold)
                }
            } else {
                // Pie de la tarjeta colapsada
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = Strings.t("screen.payment.click_to_expand"),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 4.dp)) {
        Icon(icon, null, modifier = Modifier.size(16.dp), tint = Color(0xFF94A3B8))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, style = MaterialTheme.typography.bodySmall, color = Color(0xFF475569))
    }
}
