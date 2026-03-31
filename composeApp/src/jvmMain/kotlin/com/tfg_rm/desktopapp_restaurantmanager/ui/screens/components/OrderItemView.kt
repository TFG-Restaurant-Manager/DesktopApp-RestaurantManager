package com.tfg_rm.desktopapp_restaurantmanager.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.OrderItem
import java.time.Duration
import java.time.LocalDateTime
import androidx.compose.ui.text.style.TextOverflow
import kotlinx.coroutines.delay

@Composable
fun OrderItemView(
    item: OrderItem,
    tableId: Int,
    orderId: Int,
    displayNumber: Int,
    createdAt: LocalDateTime,
    modifier: Modifier = Modifier,
    onComplete: () -> Unit = {}
) {
    // Auto-tick every second
    var now by remember { mutableStateOf(LocalDateTime.now()) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            now = LocalDateTime.now()
        }
    }

    val duration = try { Duration.between(createdAt, now) } catch (e: Exception) { Duration.ZERO }
    val totalSeconds = duration.seconds.coerceAtLeast(0)
    val totalMins    = totalSeconds / 60
    val secsRem      = totalSeconds % 60
    val hours        = totalMins / 60
    val minsRem      = totalMins % 60
    val timeDisplay  = if (totalMins < 60)
        String.format("%dm %02ds", totalMins, secsRem)
    else
        String.format("%dh %02dm", hours, minsRem)

    val borderColor = when {
        totalMins > 20 -> Color(0xFFD32F2F)
        totalMins >= 10 -> Color(0xFFF9A825)
        else -> Color(0xFFE0E0E0)
    }
    val (timeBoxBg, timeBoxBorder, timeTextColor) = when {
        totalMins > 20 -> Triple(Color(0xFFFFEBEE), Color(0xFFD32F2F), Color(0xFFD32F2F))
        totalMins >= 10 -> Triple(Color(0xFFFFF8E1), Color(0xFFF9A825), Color(0xFFF57F17))
        else -> Triple(Color(0xFFE8F5E9), Color(0xFF66BB6A), Color(0xFF2E7D32))
    }

    Card(
        modifier = modifier
            .padding(8.dp)
            .border(width = 2.dp, color = borderColor, shape = RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header: mesa name + order badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Mesa $tableId", fontWeight = FontWeight.SemiBold)
                    Text(text = "Order #$orderId", style = MaterialTheme.typography.bodySmall)
                }
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFF9800), shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = "#$displayNumber", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dish name
            Text(text = item.dish.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (!item.notes.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = item.notes, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Time box
            Card(colors = CardDefaults.cardColors(containerColor = timeBoxBg),
                modifier = Modifier.fillMaxWidth().border(2.dp, timeBoxBorder, RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                    Text(text = "Tiempo", style = MaterialTheme.typography.bodySmall, color = timeBoxBorder)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = timeDisplay, style = MaterialTheme.typography.headlineSmall, color = timeTextColor, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Entrada: ${createdAt.toLocalTime().toString().substring(0, 5)}", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onComplete,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = "✔  Completar", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
