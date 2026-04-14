package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.OrderHistoryViewModel
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.components.UiState
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import java.time.format.DateTimeFormatter
import java.util.*

private val headerBg = Color(0xFFF9FAFB)

@Composable
fun OrderHistoryScreen(viewModel: OrderHistoryViewModel, modifier: Modifier = Modifier) {
    val title by viewModel.title.collectAsState()
    val state by viewModel.orders.collectAsState()

    when (state) {
        is UiState.Error -> {
            ErrorScreen(
                title = Strings.t("screen.orderHistory.error.title"),
                message = (state as UiState.Error).message,
                primaryAction = Pair(Strings.t("reload"), { viewModel.loadOrderHistory() })
            )
        }

        UiState.Idle -> {
            viewModel.loadOrderHistory()
        }

        UiState.Loading -> {
            LoadingScreen(
                text = Strings.t("screen.orderHistory.loading")
            )
        }

        is UiState.Success<List<Order>> -> {
            val orders = (state as UiState.Success<List<Order>>).data

            val totalRevenue = orders.sumOf { it.total }
            val today = java.time.LocalDate.now()
            val todayOrders = orders.count {
                it.createdAt.toLocalDate().isEqual(today)
            }

            Column(modifier = modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 24.dp)) {

                // ── Header ──────────────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = Strings.t("screen.orderHistory.subtitle"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // ── Stat cards ──────────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HistoryStatCard(
                        Modifier.weight(1f),
                        Strings.t("screen.orderHistory.stat.total"),
                        orders.size.toString()
                    )
                    HistoryStatCard(
                        Modifier.weight(1f),
                        Strings.t("screen.orderHistory.stat.today"),
                        todayOrders.toString(),
                        Color(0xFF1565C0)
                    )
                    HistoryStatCard(
                        Modifier.weight(1f),
                        Strings.t("screen.orderHistory.stat.revenue"),
                        "%.2f €".format(totalRevenue),
                        Color(0xFF2E7D32)
                    )
                }

                // ── Table ────────────────────────────────────────────────────────────
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp
                ) {
                    Column {
                        // Header row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(headerBg)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "#",
                                Modifier.width(48.dp),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                Strings.t("screen.orderHistory.col.table"),
                                Modifier.width(80.dp),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                Strings.t("screen.orderHistory.col.items"),
                                Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                Strings.t("screen.orderHistory.col.total"),
                                Modifier.width(90.dp),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                Strings.t("screen.orderHistory.col.status"),
                                Modifier.width(110.dp),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                Strings.t("screen.orderHistory.col.date"),
                                Modifier.width(140.dp),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                        HorizontalDivider()

                        if (orders.isEmpty()) {
                            Box(
                                Modifier.fillMaxWidth().padding(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = Strings.t("screen.orderHistory.empty"),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn {
                                items(orders) { order ->
                                    OrderHistoryRow(order)
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderHistoryRow(order: Order) {
    val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.forLanguageTag("es"))
    val itemsSummary = if (order.orderItemsList.isEmpty()) "—"
    else order.orderItemsList.joinToString(", ") {
        "${it.dishName} ×${it.quantity}"
    }.let { if (it.length > 60) it.take(57) + "…" else it }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "#${order.id}",
            modifier = Modifier.width(48.dp),
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF64748B)
        )
        Text(
            text = "Mesa ${order.tableId}",
            modifier = Modifier.width(80.dp),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = itemsSummary,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2
        )
        Text(
            text = "%.2f €".format(order.total),
            modifier = Modifier.width(90.dp),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF0F172A)
        )
        Box(modifier = Modifier.width(110.dp)) {
            StatusBadge(order.status)
        }
        Text(
            text = order.createdAt.format(fmt),
            modifier = Modifier.width(140.dp),
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF64748B)
        )
    }
}

@Composable
private fun StatusBadge(status: String) {
    val (bg, fg, label) = when (status.uppercase()) {
        "CREATED" -> Triple(Color(0xFFEFF6FF), Color(0xFF1D4ED8), "Creado")
        "COOKED" -> Triple(Color(0xFFFFF7ED), Color(0xFFC2410C), "Cocinado")
        "DELIVERED" -> Triple(Color(0xFFF0FDF4), Color(0xFF15803D), "Entregado")
        "PAID" -> Triple(Color(0xFFF0FDF4), Color(0xFF166534), "Pagado")
        else -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), status)
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(label, color = fg, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun HistoryStatCard(
    modifier: Modifier,
    label: String,
    value: String,
    valueColor: Color = Color(0xFF111827)
) {
    Surface(modifier = modifier, shape = RoundedCornerShape(12.dp), shadowElevation = 2.dp) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}
