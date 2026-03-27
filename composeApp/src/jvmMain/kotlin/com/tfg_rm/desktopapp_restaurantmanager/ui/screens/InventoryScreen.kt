package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Ingredient
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.InventoryViewModel
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings

private val Orange = Color(0xFFF97316)

@Composable
fun InventoryScreen(viewModel: InventoryViewModel, modifier: Modifier = Modifier) {
    val ingredients by viewModel.ingredients.collectAsState()
    val categories  by viewModel.categories.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadInventory() }

    var selectedCategory    by remember { mutableStateOf(Strings.t("screen.inventory.filter.all")) }
    var showAddDialog       by remember { mutableStateOf(false) }
    var showCategoryDialog  by remember { mutableStateOf(false) }
    var editTarget          by remember { mutableStateOf<Ingredient?>(null) }
    var deleteTarget        by remember { mutableStateOf<Ingredient?>(null) }

    val belowMinimum = ingredients.filter { it.stockQuantity < it.minimumStock }
    val totalValue   = ingredients.sumOf { it.stockQuantity * it.costUnit }
    val displayed    = if (selectedCategory == Strings.t("screen.inventory.filter.all")) ingredients
                       else ingredients.filter { it.category == selectedCategory }

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 24.dp)) {

        // ── Header ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = Strings.t("screen.inventory.title"), 
                    style = MaterialTheme.typography.headlineLarge, 
                    fontWeight = FontWeight.ExtraBold, 
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = Strings.t("screen.inventory.subtitle"), 
                    style = MaterialTheme.typography.bodyLarge, 
                    color = Color(0xFF64748B)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedButton(
                    onClick = { showCategoryDialog = true },
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF334155)),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(text = Strings.t("screen.inventory.manage_categories"), fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Orange),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(text = "+", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = Strings.t("screen.inventory.add_ingredient"), color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Stats cards ──────────────────────────────────────────────────────
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            StatCard(
                modifier = Modifier.weight(1f), 
                label = Strings.t("screen.inventory.stat.total_products"), 
                value = "${ingredients.size}",
                icon = "📦",
                iconBg = Color(0xFFEFF6FF),
                iconColor = Color(0xFF3B82F6)
            )
            StatCard(
                modifier = Modifier.weight(1f), 
                label = Strings.t("screen.inventory.stat.below_minimum"), 
                value = "${belowMinimum.size}", 
                valueColor = if (belowMinimum.isNotEmpty()) Color(0xFFDC2626) else Color(0xFF0F172A),
                icon = "⚠",
                iconBg = Color(0xFFFEF2F2),
                iconColor = Color(0xFFEF4444)
            )
            StatCard(
                modifier = Modifier.weight(1f), 
                label = Strings.t("screen.inventory.stat.total_value"), 
                value = String.format("%.0f€", totalValue),
                icon = "↗",
                iconBg = Color(0xFFDCFCE7),
                iconColor = Color(0xFF22C55E)
            )
            StatCard(
                modifier = Modifier.weight(1f), 
                label = Strings.t("screen.inventory.stat.categories"), 
                value = "${categories.size}",
                icon = "📉", 
                iconBg = Color(0xFFFFEDD5),
                iconColor = Color(0xFFF97316)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Low stock alert ───────────────────────────────────────────────────
        if (belowMinimum.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = BorderStroke(1.dp, Color(0xFFFECACA)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("⚠", fontSize = 20.sp, color = Color(0xFFEF4444))
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(text = Strings.t("screen.inventory.alert.low_stock_title"), fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C), fontSize = 16.sp)
                        Spacer(Modifier.height(4.dp))
                        Row {
                            Text(text = Strings.t("screen.inventory.alert.low_stock_text_prefix"), style = MaterialTheme.typography.bodyMedium, color = Color(0xFFB91C1C))
                            Text(text = " " + belowMinimum.joinToString(", ") { it.name }, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFFB91C1C))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // ── Category filter pills ─────────────────────────────────────────────
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            LazyRow(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val allCategories = listOf(Strings.t("screen.inventory.filter.all")) + categories
                items(allCategories) { cat ->
                    val selected = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (selected) Orange else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            color = if (selected) Color.White else Color(0xFF334155),
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Table ─────────────────────────────────────────────────────────────
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Table header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = Strings.t("screen.inventory.table.product"),      modifier = Modifier.weight(2f),   fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    Text(text = Strings.t("screen.inventory.table.category"),     modifier = Modifier.weight(1.5f), fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    Text(text = Strings.t("screen.inventory.table.quantity"),     modifier = Modifier.weight(1.5f), fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    Text(text = Strings.t("screen.inventory.table.minimum"),      modifier = Modifier.weight(1.5f), fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    Text(text = Strings.t("screen.inventory.table.price_unit"),   modifier = Modifier.weight(1.5f), fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    Text(text = Strings.t("screen.inventory.table.total_value"),  modifier = Modifier.weight(1.5f), fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    Text(text = Strings.t("screen.inventory.table.status"),       modifier = Modifier.weight(1f),   fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.weight(1.2f))
                }
                
                Divider(color = Color(0xFFE2E8F0))
                
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(displayed) { ingredient ->
                        val isLow = ingredient.stockQuantity <= ingredient.minimumStock
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = ingredient.name, modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                            
                            Box(modifier = Modifier.weight(1.5f)) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = Color(0xFFF8FAFC)
                                ) {
                                    Text(
                                        text = ingredient.category, 
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        color = Color(0xFF64748B),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                            
                            Text(
                                text = "${ingredient.stockQuantity.toDisplayString()} ${ingredient.unit}",
                                modifier = Modifier.weight(1.5f),
                                fontWeight = FontWeight.Bold,
                                color = if (isLow) Color(0xFFDC2626) else Color(0xFF0F172A)
                            )
                            
                            Text(
                                text = "${ingredient.minimumStock.toDisplayString()} ${ingredient.unit}",
                                modifier = Modifier.weight(1.5f),
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                            
                            Text(
                                text = "${ingredient.costUnit}€", 
                                modifier = Modifier.weight(1.5f), 
                                color = Color(0xFF334155),
                                fontWeight = FontWeight.Medium
                            )
                            
                            Text(
                                text = String.format("%.2f€", ingredient.stockQuantity * ingredient.costUnit),
                                modifier = Modifier.weight(1.5f),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            
                            Box(modifier = Modifier.weight(1f)) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isLow) Color(0xFFFEE2E2) else Color(0xFFDCFCE7)
                                ) {
                                    Text(
                                        text = if (isLow) Strings.t("screen.inventory.status.low") else Strings.t("screen.inventory.status.ok"),
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                        color = if (isLow) Color(0xFFDC2626) else Color(0xFF16A34A),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            
                            Row(modifier = Modifier.weight(1.2f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(onClick = { editTarget = ingredient }, modifier = Modifier.size(36.dp)) {
                                    Text("✎", color = Color(0xFF3B82F6), fontSize = 18.sp)
                                }
                                IconButton(onClick = { deleteTarget = ingredient }, modifier = Modifier.size(36.dp)) {
                                    Text("🗑", color = Color(0xFFEF4444), fontSize = 18.sp)
                                }
                            }
                        }
                        Divider(color = Color(0xFFE2E8F0))
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        IngredientFormDialog(
            title = Strings.t("screen.ingredient.new_title"),
            categories = categories,
            initial = null,
            onDismiss = { showAddDialog = false },
            onConfirm = { ingredient ->
                viewModel.addIngredient(ingredient)
                showAddDialog = false
            }
        )
    }

    editTarget?.let { ing ->
        IngredientFormDialog(
            title = Strings.t("screen.ingredient.edit_title"),
            categories = categories,
            initial = ing,
            onDismiss = { editTarget = null },
            onConfirm = { updated ->
                viewModel.updateIngredient(updated.copy(id = ing.id, restaurantId = ing.restaurantId))
                editTarget = null
            }
        )
    }

    deleteTarget?.let { ing ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            title = { Text(text = Strings.t("screen.ingredient.delete_title")) },
            text  = { Text(text = String.format(Strings.t("screen.ingredient.delete_confirm"), ing.name)) },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteIngredient(ing.id); deleteTarget = null }) {
                    Text(text = Strings.t("screen.inventory.action.delete"), color = Color(0xFFD32F2F))
                }
            },
            dismissButton = { TextButton(onClick = { deleteTarget = null }) { Text(text = Strings.t("screen.ingredient.form.cancel")) } }
        )
    }

    if (showCategoryDialog) {
        CategoryManagerDialog(
            categories = categories,
            onDismiss = { showCategoryDialog = false },
            onAdd = { viewModel.addCategory(it) },
            onDelete = { viewModel.deleteCategory(it) }
        )
    }
}

private fun Double.toDisplayString(): String =
    if (this == kotlin.math.floor(this)) this.toInt().toString() else this.toString()

@Composable
private fun StatCard(
    modifier: Modifier, 
    label: String, 
    value: String, 
    valueColor: Color = Color(0xFF0F172A),
    icon: String = "⏺",
    iconBg: Color = Color(0xFFF1F5F9),
    iconColor: Color = Color(0xFF64748B)
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 24.sp, color = iconColor)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = valueColor)
            }
        }
    }
}

@Composable
private fun IngredientFormDialog(
    title: String,
    categories: List<String>,
    initial: Ingredient?,
    onDismiss: () -> Unit,
    onConfirm: (Ingredient) -> Unit
) {
    var name         by remember { mutableStateOf(initial?.name         ?: "") }
    var category     by remember { mutableStateOf(initial?.category     ?: categories.firstOrNull() ?: "") }
    var unit         by remember { mutableStateOf(initial?.unit         ?: "kg") }
    var stock        by remember { mutableStateOf(initial?.stockQuantity?.toString() ?: "") }
    var minStock     by remember { mutableStateOf(initial?.minimumStock?.toString()  ?: "") }
    var cost         by remember { mutableStateOf(initial?.costUnit?.toString()       ?: "") }
    var showCatPicker by remember { mutableStateOf(false) }
    var error        by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(value = name,     onValueChange = { name = it;     error = null }, label = { Text(Strings.t("screen.ingredient.form.name")) },         singleLine = true, modifier = Modifier.fillMaxWidth())
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(value = category, onValueChange = { category = it }, label = { Text(Strings.t("screen.ingredient.form.category")) }, singleLine = true, modifier = Modifier.weight(1f))
                    Column {
                        if (showCatPicker) {
                            categories.forEach { cat ->
                                TextButton(onClick = { category = cat; showCatPicker = false }) { Text(text = cat) }
                            }
                        } else {
                            OutlinedButton(onClick = { showCatPicker = true }) { Text(text = "▾") }
                        }
                    }
                }
                TextField(value = unit,     onValueChange = { unit = it;     error = null }, label = { Text(Strings.t("screen.ingredient.form.unit")) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(value = stock,    onValueChange = { stock = it;    error = null }, label = { Text(Strings.t("screen.ingredient.form.stock_current")) },   singleLine = true, modifier = Modifier.weight(1f))
                    TextField(value = minStock, onValueChange = { minStock = it; error = null }, label = { Text(Strings.t("screen.ingredient.form.stock_minimum")) },  singleLine = true, modifier = Modifier.weight(1f))
                }
                TextField(value = cost,     onValueChange = { cost = it;     error = null }, label = { Text(Strings.t("screen.ingredient.form.price_unit")) }, singleLine = true, modifier = Modifier.fillMaxWidth())
                if (error != null) Text(text = error!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val stockD   = stock.toDoubleOrNull()
                val minD     = minStock.toDoubleOrNull()
                val costD    = cost.toDoubleOrNull()
                when {
                    name.isBlank()   -> error = Strings.t("screen.ingredient.form.error.name_required")
                    category.isBlank() -> error = Strings.t("screen.ingredient.form.error.category_required")
                    unit.isBlank()   -> error = Strings.t("screen.ingredient.form.error.unit_required")
                    stockD == null   -> error = Strings.t("screen.ingredient.form.error.stock_invalid")
                    minD == null     -> error = Strings.t("screen.ingredient.form.error.min_stock_invalid")
                    costD == null    -> error = Strings.t("screen.ingredient.form.error.price_invalid")
                    else -> onConfirm(Ingredient(id = 0, restaurantId = 1, name = name.trim(), unit = unit.trim(), stockQuantity = stockD, costUnit = costD, category = category.trim(), minimumStock = minD))
                }
            }) { Text(text = Strings.t("screen.ingredient.form.save")) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(text = Strings.t("screen.ingredient.form.cancel")) } }
    )
}

@Composable
private fun CategoryManagerDialog(
    categories: List<String>,
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    var newCat by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = Strings.t("screen.category_manager.title")) },
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(value = newCat, onValueChange = { newCat = it }, label = { Text(Strings.t("screen.category_manager.new_label")) }, singleLine = true, modifier = Modifier.weight(1f))
                    Button(
                        onClick = { if (newCat.isNotBlank()) { onAdd(newCat.trim()); newCat = "" } },
                        colors = ButtonDefaults.buttonColors(containerColor = Orange)
                    ) { Text(text = Strings.t("screen.category_manager.add_button")) }
                }
                Spacer(modifier = Modifier.height(12.dp))
                if (categories.isEmpty()) {
                    Text(text = Strings.t("screen.category_manager.no_categories"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    LazyColumn(modifier = Modifier.height(200.dp)) {
                        items(categories) { cat ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = cat, style = MaterialTheme.typography.bodyMedium)
                                TextButton(onClick = { onDelete(cat) }) {
                                    Text(text = Strings.t("screen.category_manager.delete"), color = Color(0xFFD32F2F), fontSize = 12.sp)
                                }
                            }
                            Divider()
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text(text = Strings.t("screen.category_manager.close")) } }
    )
}
