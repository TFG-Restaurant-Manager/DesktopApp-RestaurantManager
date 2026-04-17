package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.*
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import org.koin.compose.viewmodel.koinViewModel

private val primaryOrange = Color(0xFFFF6A00)
private val selectedBg = Color(0xFFFFF2E6)
private val unselectedText = Color(0xFF374151)

@Composable
fun MainScreen(logOut: () -> Unit) {
    var selected by remember { mutableStateOf("orders") }

    val dishesViewModel: DishesViewModel = koinViewModel()
    val employeesViewModel: EmployeesViewModel = koinViewModel()
    val inventoryViewModel: InventoryViewModel = koinViewModel()
    val economyViewModel: EconomyViewModel = koinViewModel()
    val ordersViewModel: OrdersViewModel = koinViewModel()
    val tablesViewModel: TablesViewModel = koinViewModel()
    val newOrderViewModel: NewOrderViewModel = koinViewModel()
    val orderHistoryViewModel: OrderHistoryViewModel = koinViewModel()

    val logoutResetVMs = {
        dishesViewModel.resetState()
        employeesViewModel.resetState()
        inventoryViewModel.resetState()
        //economyViewModel.resetState()
        ordersViewModel.resetState()
        tablesViewModel.resetState()
        //newOrderViewModel.resetState()
        orderHistoryViewModel.resetState()
    }

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
                text = Strings.t("screen.main.controlpanel"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            NavItem(Strings.t("screen.newOrden.title"), selected == "newOrder") { selected = "newOrder" }
            NavItem(Strings.t("screen.orders.title"), selected == "orders") { selected = "orders" }
            NavItem(Strings.t("screen.tables.title"), selected == "tables") { selected = "tables" }
            NavItem(Strings.t("screen.employees.title"), selected == "employees") { selected = "employees" }
            NavItem(Strings.t("screen.schedule.title"), selected == "schedule") { selected = "schedule" }
            NavItem(Strings.t("screen.inventory.title"), selected == "inventory") { selected = "inventory" }
            NavItem(Strings.t("screen.dishes.title"), selected == "dishes") { selected = "dishes" }
            NavItem(Strings.t("screen.economy.title"), selected == "economy") { selected = "economy" }
            NavItem(Strings.t("screen.orderHistory.title"), selected == "orderHistory") { selected = "orderHistory" }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        logOut()
                        logoutResetVMs()
                    }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = Strings.t("nav.logout"), color = logoutColor)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            when (selected) {
                "newOrder" -> NewOrderScreen(
                    viewModel = newOrderViewModel,
                    modifier = Modifier.fillMaxWidth()
                )

                "orders" -> OrdersScreen(ordersViewModel, Modifier.fillMaxWidth())
                "tables" -> TablesScreen(tablesViewModel, Modifier.fillMaxWidth())
                "employees" -> EmployeesScreen(employeesViewModel, Modifier.fillMaxWidth())
                "schedule" -> ScheduleScreen(employeesViewModel, Modifier.fillMaxWidth())
                "inventory" -> InventoryScreen(inventoryViewModel, Modifier.fillMaxWidth())
                "dishes" -> DishesScreen(dishesViewModel, Modifier.fillMaxWidth())
                "economy" -> EconomyScreen(economyViewModel, Modifier.fillMaxWidth())
                "orderHistory" -> OrderHistoryScreen(orderHistoryViewModel, Modifier.fillMaxWidth())
                else -> OrdersScreen(ordersViewModel, Modifier.fillMaxWidth())
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
