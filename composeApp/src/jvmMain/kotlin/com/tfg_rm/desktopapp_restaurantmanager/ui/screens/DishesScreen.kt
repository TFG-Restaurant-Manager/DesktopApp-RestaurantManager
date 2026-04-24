package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Category
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.DishIngredient
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Dishes
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Ingredient
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.DishesViewModel
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.components.UiState
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings

private val dishOrange = Color(0xFFFF6A00)
private val dishHeaderBg = Color(0xFFF9FAFB)

// ─────────────────────────────────────────────────────────────────────────────
// Main screen
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun DishesScreen(viewModel: DishesViewModel, modifier: Modifier = Modifier) {
    val stateDishes by viewModel.dishes.collectAsState()
    val stateIngredients by viewModel.availableIngredients.collectAsState()

    when (stateDishes) {
        is UiState.Error -> {
            ErrorScreen(
                title = Strings.t("screen.dish.error.generic"),
                message = (stateDishes as UiState.Error).message,
                primaryAction = Pair(Strings.t("reload")) { viewModel.loadDishes() }
            )
        }

        UiState.Idle -> {
            viewModel.loadDishes()
        }

        UiState.Loading -> {
            LoadingScreen(
                text = Strings.t("screen.dish.loading.message")
            )
        }

        is UiState.Success<List<Dishes>> -> {
            val dishes = (stateDishes as UiState.Success).data
            val availableIngredients = (stateIngredients as UiState.Success<List<Ingredient>>).data

            var selectedCategory by remember { mutableStateOf(Category(0, Strings.t("screen.dishes.filter.all"))) }
            var showAddDialog by remember { mutableStateOf(false) }
            var editTarget by remember { mutableStateOf<Dishes?>(null) }
            var deleteTarget by remember { mutableStateOf<Dishes?>(null) }

            val categories = dishes.map { it.category }.distinct().sortedBy { it.name }
            val displayed = if (selectedCategory.name == Strings.t("screen.dishes.filter.all")) dishes
            else dishes.filter { it.category.name == selectedCategory.name }
            val availableCount = dishes.count { it.available }
            val unavailableCount = dishes.count { !it.available }

            Column(modifier = modifier) {

                // ── Header ──────────────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = Strings.t("screen.dishes.title"),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = Strings.t("screen.dishes.subtitle"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Button(
                        onClick = { showAddDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = dishOrange)
                    ) {
                        Text(Strings.t("screen.dishes.add_button"), color = Color.White)
                    }
                }

                // ── Stat cards ──────────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DishStatCard(Modifier.weight(1f), Strings.t("screen.dishes.stat.total"), dishes.size.toString())
                    DishStatCard(
                        Modifier.weight(1f),
                        Strings.t("screen.dishes.stat.available"),
                        availableCount.toString(),
                        Color(0xFF2E7D32)
                    )
                    DishStatCard(
                        Modifier.weight(1f),
                        Strings.t("screen.dishes.stat.unavailable"),
                        unavailableCount.toString(),
                        Color(0xFFC62828)
                    )
                    DishStatCard(
                        Modifier.weight(1f),
                        Strings.t("screen.dishes.stat.categories"),
                        categories.size.toString()
                    )
                }

                // ── Category filter pills ────────────────────────────────────────────
                LazyRow(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val allFilter = Category(0, Strings.t("screen.dishes.filter.all"))
                    item {
                        DishFilterPill(allFilter, selectedCategory == allFilter) {
                            selectedCategory = allFilter
                        }
                    }
                    items(categories) { cat ->
                        DishFilterPill(cat!!, selectedCategory == cat) { selectedCategory = cat }
                    }
                }

                // ── Table ────────────────────────────────────────────────────────────
                Surface(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp
                ) {
                    Column {
                        // Header row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(dishHeaderBg)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                Strings.t("screen.dishes.table.name"),
                                Modifier.weight(2f),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                Strings.t("screen.dishes.table.description"),
                                Modifier.weight(3f),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                Strings.t("screen.dishes.table.category"),
                                Modifier.weight(1.5f),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                Strings.t("screen.dishes.table.price"),
                                Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                Strings.t("screen.dishes.table.ingredients"),
                                Modifier.weight(2f),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                Strings.t("screen.dishes.table.status"),
                                Modifier.weight(1f),
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                "",// Hueco para opciones de los platos
                                Modifier.weight(1.5f)
                            )
                        }
                        HorizontalDivider()

                        if (displayed.isEmpty()) {
                            Box(Modifier.fillMaxWidth().padding(36.dp), contentAlignment = Alignment.Center) {
                                Text(
                                    Strings.t("screen.dishes.empty"),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            LazyColumn {
                                items(displayed) { dish ->
                                    DishRow(
                                        dish = dish,
                                        onEdit = { editTarget = dish },
                                        onDelete = { deleteTarget = dish }
                                    )
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }

            // ── Add / Edit dialog ────────────────────────────────────────────────────
            if (showAddDialog || editTarget != null) {
                DishFormDialog(
                    dish = editTarget,
                    availableIngredients = availableIngredients,
                    onDismiss = { showAddDialog = false; editTarget = null },
                    categories = categories,
                    onSave = { saved ->
                        if (editTarget != null) viewModel.updateDish(saved) else viewModel.addDish(saved)
                        showAddDialog = false
                        editTarget = null
                    }
                )
            }

            // ── Delete confirmation ──────────────────────────────────────────────────
            deleteTarget?.let { dish ->
                AlertDialog(
                    onDismissRequest = { deleteTarget = null },
                    title = { Text(Strings.t("screen.dish.delete_title")) },
                    text = { Text(String.format(Strings.t("screen.dish.delete_confirm"), dish.name)) },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.deleteDish(dish.id); deleteTarget = null },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                        ) { Text("OK", color = Color.White) }
                    },
                    dismissButton = {
                        TextButton(onClick = { deleteTarget = null }) {
                            Text(Strings.t("screen.dish.form.cancel"))
                        }
                    }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Stat card
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DishStatCard(
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

// ─────────────────────────────────────────────────────────────────────────────
// Filter pill
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DishFilterPill(label: Category, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) dishOrange else Color(0xFFF3F4F6)
    val textColor = if (selected) Color.White else Color(0xFF374151)

    Button(
        onClick = onClick,
        // Usamos una altura mínima pequeña para que parezca una píldora (Pill)
        modifier = Modifier.height(32.dp),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = bg,
            contentColor = textColor
        ),
        // Eliminamos la elevación para que se mantenga plano como tu diseño original
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 0.dp,
            pressedElevation = 2.dp,
            hoveredElevation = 1.dp
        ),
        // Ajustamos los paddings internos para que coincidan con tu Box
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
    ) {
        Text(
            text = label.name,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Table row
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DishRow(dish: Dishes, onEdit: () -> Unit, onDelete: () -> Unit) {
    val availableBg = if (dish.available) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val availableColor = if (dish.available) Color(0xFF2E7D32) else Color(0xFFC62828)
    val statusLabel = if (dish.available) Strings.t("screen.dishes.status.available")
    else Strings.t("screen.dishes.status.unavailable")
    val ingredientsSummary = if (dish.ingredients.isEmpty()) "—"
    else dish.ingredients.joinToString(", ") { "${it.ingredient.name} (${it.quantity} ${it.ingredient.unit})" }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            dish.name,
            Modifier.weight(2f),
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = dish.description?.take(70) + if ((dish.description?.length ?: 0) > 70) "…" else "",
            modifier = Modifier.weight(3f),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
        Text(dish.category.name, Modifier.weight(1.5f), style = MaterialTheme.typography.bodySmall)
        Text(
            "%.2f €".format(dish.price),
            Modifier.weight(1f),
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = ingredientsSummary,
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2
        )
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            Box(
                Modifier
                    .background(availableBg, RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    statusLabel,
                    color = availableColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        Column(
            modifier = Modifier.weight(1.5f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextButton(onClick = onEdit) {
                Text(
                    Strings.t("screen.dishes.action.edit"),
                    color = dishOrange,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                )
            }
            TextButton(onClick = onDelete) {
                Text(
                    Strings.t("screen.dishes.action.delete"),
                    color = Color(0xFFD32F2F),
                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Add / Edit dialog
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DishFormDialog(
    dish: Dishes?,
    categories: List<Category>,
    availableIngredients: List<Ingredient>,
    onDismiss: () -> Unit,
    onSave: (Dishes) -> Unit
) {
    val isEdit = dish != null
    var expanded by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf(dish?.name ?: "") }
    var description by remember { mutableStateOf(dish?.description ?: "") }
    var category by remember { mutableStateOf(dish?.category ?: Category(0, "---")) }
    var price by remember { mutableStateOf(dish?.price?.toString() ?: "") }
    var available by remember { mutableStateOf(dish?.available ?: true) }
    var dishIngredients by remember { mutableStateOf(dish?.ingredients ?: emptyList()) }

    var dropdownExpanded by remember { mutableStateOf(false) }
    var selectedIngredient by remember { mutableStateOf<Ingredient?>(null) }
    var ingredientQuantity by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf("") }
    var priceError by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (isEdit) Strings.t("screen.dish.edit_title") else Strings.t("screen.dish.new_title"),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .width(580.dp)
                    .heightIn(max = 540.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; nameError = "" },
                    label = { Text(Strings.t("screen.dish.form.name")) },
                    isError = nameError.isNotEmpty(),
                    supportingText = if (nameError.isNotEmpty()) {
                        { Text(nameError, color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(Strings.t("screen.dish.form.description")) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                // Category + Price in one row
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = category.name,
                            onValueChange = { },
                            readOnly = true,
                            label = { Text(Strings.t("screen.dish.form.category")) },
                            trailingIcon = {
                                IconButton(onClick = { expanded = true }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null
                                    )
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clickable { expanded = true }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.fillMaxWidth(0.5f)
                        ) {
                            categories.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion.name) },
                                    onClick = {
                                        category = opcion
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it; priceError = "" },
                        label = { Text(Strings.t("screen.dish.form.price")) },
                        isError = priceError.isNotEmpty(),
                        supportingText = if (priceError.isNotEmpty()) {
                            { Text(priceError, color = MaterialTheme.colorScheme.error) }
                        } else null,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Available toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = available,
                        onCheckedChange = { available = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = dishOrange)
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(Strings.t("screen.dish.form.available"), style = MaterialTheme.typography.bodyMedium)
                }

                HorizontalDivider()

                // Ingredients section title
                Text(
                    Strings.t("screen.dish.form.ingredients_section"),
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleSmall
                )

                // Ingredient picker row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = it },
                        modifier = Modifier.weight(2.5f)
                    ) {
                        OutlinedTextField(
                            value = selectedIngredient?.let { "${it.name} (${it.unit})" }
                                ?: Strings.t("screen.dish.form.select_ingredient"),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(Strings.t("screen.dish.form.select_ingredient")) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            if (availableIngredients.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("—", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    onClick = {}
                                )
                            } else {
                                availableIngredients.forEach { ing ->
                                    DropdownMenuItem(
                                        text = { Text("${ing.name} (${ing.unit})") },
                                        onClick = { selectedIngredient = ing; dropdownExpanded = false }
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = ingredientQuantity,
                        onValueChange = { ingredientQuantity = it },
                        label = { Text(Strings.t("screen.dish.form.quantity")) },
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            val ing = selectedIngredient
                            val qty = ingredientQuantity.replace(',', '.').toDoubleOrNull()
                            if (ing != null && qty != null && qty > 0) {
                                dishIngredients = if (dishIngredients.any { it.ingredient.id == ing.id }) {
                                    dishIngredients.map { if (it.ingredient.id == ing.id) it.copy(quantity = qty) else it }
                                } else {
                                    dishIngredients + DishIngredient(ing, qty)
                                }
                                selectedIngredient = null
                                ingredientQuantity = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = dishOrange),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(Strings.t("screen.dish.form.add_ingredient"), color = Color.White)
                    }
                }

                // Added ingredients list
                if (dishIngredients.isEmpty()) {
                    Text(
                        Strings.t("screen.dish.form.no_ingredients"),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        dishIngredients.forEach { di ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "${di.ingredient.name}  ×  ${di.quantity} ${di.ingredient.unit}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                                TextButton(
                                    onClick = {
                                        dishIngredients =
                                            dishIngredients.filter { it.ingredient.id != di.ingredient.id }
                                    },
                                    contentPadding = PaddingValues(horizontal = 4.dp)
                                ) {
                                    Text("✕", color = Color(0xFFD32F2F), style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    var valid = true
                    if (name.isBlank()) {
                        nameError = Strings.t("screen.dish.form.error.name_required")
                        valid = false
                    }
                    val priceVal = price.replace(',', '.').toDoubleOrNull()
                    if (priceVal == null || priceVal < 0.0) {
                        priceError = Strings.t("screen.dish.form.error.price_invalid")
                        valid = false
                    }
                    if (valid) {
                        onSave(
                            Dishes(
                                id = dish?.id ?: 0,
                                name = name.trim(),
                                description = description.trim(),
                                category = category,
                                price = priceVal!!,
                                available = available,
                                ingredients = dishIngredients
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = dishOrange)
            ) {
                Text(Strings.t("screen.dish.form.save"), color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(Strings.t("screen.dish.form.cancel")) }
        }
    )
}
