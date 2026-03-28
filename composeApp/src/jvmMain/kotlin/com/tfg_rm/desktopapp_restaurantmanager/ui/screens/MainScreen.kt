package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tfg_rm.desktopapp_restaurantmanager.ui.navigation.AppScreens
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.*
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import org.koin.compose.viewmodel.koinViewModel

private val primaryOrange = Color(0xFFFF6A00)
private val selectedBg = Color(0xFFFFF2E6)
private val unselectedText = Color(0xFF374151)

@Composable
fun MainScreen(navigate: (String) -> Unit) {
    var selected by remember { mutableStateOf("orders") }

    // Color constants matching the design
    val logoutColor = Color(0xFFE53935)

    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .width(240.dp)
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp)
        ) {
            // App title
            Text(
                text = "RestaurantPro",
                color = primaryOrange,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Panel de Control",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            NavItem(Strings.t("screen.orders.title"), selected == "orders") { selected = "orders" }
            NavItem(Strings.t("screen.tables.title"), selected == "tables") { selected = "tables" }
            NavItem(Strings.t("screen.employees.title"), selected == "employees") { selected = "employees" }
            NavItem(Strings.t("screen.schedule.title"), selected == "schedule") { selected = "schedule" }
            NavItem(Strings.t("screen.inventory.title"), selected == "inventory") { selected = "inventory" }
            NavItem(Strings.t("screen.dishes.title"),    selected == "dishes")    { selected = "dishes" }
            NavItem(Strings.t("screen.economy.title"), selected == "economy") { selected = "economy" }
            NavItem(Strings.t("screen.orderHistory.title"), selected == "orderHistory") { selected = "orderHistory" }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navigate(AppScreens.LoginScreen.route) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = Strings.t("nav.logout"), color = logoutColor)
            }
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
        ) {
            when (selected) {
                "orders" -> OrdersScreen(koinViewModel(), Modifier.fillMaxWidth())
                "tables" -> TablesScreen(koinViewModel(), Modifier.fillMaxWidth())
                "employees" -> EmployeesScreen(koinViewModel(), Modifier.fillMaxWidth())
                "schedule" -> ScheduleScreen(koinViewModel(), Modifier.fillMaxWidth())
                "inventory" -> InventoryScreen(koinViewModel(), Modifier.fillMaxWidth())                "dishes"       -> DishesScreen(koinViewModel(), Modifier.fillMaxWidth())                "economy" -> EconomyScreen(koinViewModel(), Modifier.fillMaxWidth())
                "orderHistory" -> OrderHistoryScreen(koinViewModel(), Modifier.fillMaxWidth())
                else -> OrdersScreen(koinViewModel(), Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (selected) selectedBg else Color.Transparent
    val textColor = if (selected) primaryOrange else unselectedText
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(bg, shape = RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = textColor, style = MaterialTheme.typography.bodyLarge)
    }
}
