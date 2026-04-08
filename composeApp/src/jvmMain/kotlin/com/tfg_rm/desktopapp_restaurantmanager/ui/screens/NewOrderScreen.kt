@file:OptIn(ExperimentalMaterial3Api::class)

package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Dishes
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.DishIngredient
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.DraftItem
import com.tfg_rm.desktopapp_restaurantmanager.domain.NewOrderStep
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.NewOrderViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.OrderType

private val orange = Color(0xFFFF6A00)
private val orangeLight = Color(0xFFFFF2E6)
private val grayBg = Color(0xFFF9FAFB)
private val grayBorder = Color(0xFFE2E8F0)

@Composable
fun NewOrderScreen(
    viewModel: NewOrderViewModel,
    modifier: Modifier = Modifier
) {
    val step     by viewModel.step.collectAsState()
    val drafts   by viewModel.draftItems.collectAsState()

    // Top summary bar
    Column(modifier = modifier.fillMaxSize()) {
        // ── Header ──────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Nuevo Pedido",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0F172A)
                )
                Text(
                    when (step) {
                        NewOrderStep.TYPE    -> "Paso 1 — Tipo y destino"
                        NewOrderStep.DISHES  -> "Paso 2 — Elige los platos"
                        NewOrderStep.PAYMENT -> "Paso 3 — Pago"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B)
                )
            }
            // Step indicator dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                StepDot(1, step.ordinal + 1)
                Box(Modifier.width(24.dp).height(2.dp).background(grayBorder))
                StepDot(2, step.ordinal + 1)
                Box(Modifier.width(24.dp).height(2.dp).background(grayBorder))
                StepDot(3, step.ordinal + 1)
            }
        }

        HorizontalDivider()

        // ── Content area ────────────────────────────────────────────────────
        when (step) {
            NewOrderStep.TYPE    -> StepTypeScreen(viewModel)
            NewOrderStep.DISHES  -> StepDishesScreen(viewModel)
            NewOrderStep.PAYMENT -> StepPaymentScreen(viewModel)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 1 — Type & destination
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StepTypeScreen(viewModel: NewOrderViewModel) {
    val orderType      by viewModel.orderType.collectAsState()
    val selectedTableId by viewModel.selectedTableId.collectAsState()
    val tables         by viewModel.tables.collectAsState()
    val deliveryAddr   by viewModel.deliveryAddress.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // ── Order type pills ──────────────────────────────────────────────
        Text("Tipo de pedido", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = Color(0xFF0F172A))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OrderType.entries.forEach { type ->
                val selected = orderType == type
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selected) orange else grayBg,
                    shadowElevation = if (selected) 4.dp else 1.dp,
                    modifier = Modifier
                        .height(52.dp)
                        .clickable { viewModel.selectOrderType(type) }
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            type.label,
                            fontWeight = FontWeight.SemiBold,
                            color = if (selected) Color.White else Color(0xFF374151)
                        )
                    }
                }
            }
        }

        // ── Table map (only for TABLE type) ──────────────────────────────
        if (orderType == OrderType.TABLE) {
            Text("Selecciona una mesa", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = Color(0xFF0F172A))

            val maxCol = tables.maxOfOrNull { it.posX } ?: 6
            val maxRow = tables.maxOfOrNull { it.posY } ?: 5
            val cellDp = 100.dp
            val gapDp  = 12.dp

            Surface(
                shape = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp,
                color = Color.White,
                modifier = Modifier.wrapContentSize()
            ) {
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .size(
                            width  = (cellDp + gapDp) * maxCol - gapDp + 32.dp,
                            height = (cellDp + gapDp) * maxRow - gapDp + 32.dp
                        )
                ) {
                    // empty cells
                    for (r in 1..maxRow) {
                        for (c in 1..maxCol) {
                            if (tables.none { it.posX == c && it.posY == r }) {
                                Box(
                                    modifier = Modifier
                                        .offset(
                                            x = ((c - 1) * (cellDp + gapDp).value).dp,
                                            y = ((r - 1) * (cellDp + gapDp).value).dp
                                        )
                                        .size(cellDp)
                                        .border(1.dp, Color(0xFFF0F0F0), RoundedCornerShape(10.dp))
                                )
                            }
                        }
                    }
                    // tables
                    tables.forEach { table ->
                        val isSelected = selectedTableId == table.id
                        Box(
                            modifier = Modifier
                                .offset(
                                    x = ((table.posX - 1) * (cellDp + gapDp).value).dp,
                                    y = ((table.posY - 1) * (cellDp + gapDp).value).dp
                                )
                                .size(cellDp)
                                .background(
                                    color = if (isSelected) orange else Color(0xFFFF9950),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) Color(0xFFCC4400) else Color.Transparent,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { viewModel.selectTable(table.id) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    table.id.toString(),
                                    fontSize   = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = Color.White
                                )
                                Text(
                                    "👤 ${table.capacity}",
                                    fontSize = 12.sp,
                                    color    = Color.White.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── Delivery address ──────────────────────────────────────────────
        if (orderType == OrderType.DELIVERY) {
            OutlinedTextField(
                value         = deliveryAddr,
                onValueChange = viewModel::setDeliveryAddress,
                label         = { Text("Dirección de entrega") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(10.dp)
            )
        }

        // ── Next button ───────────────────────────────────────────────────
        val canContinue = orderType != OrderType.TABLE || selectedTableId != null
        Button(
            onClick  = viewModel::confirmType,
            enabled  = canContinue,
            colors   = ButtonDefaults.buttonColors(containerColor = orange),
            shape    = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Continuar → Elegir platos", color = Color.White, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 2 — Dishes
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StepDishesScreen(viewModel: NewOrderViewModel) {
    val dishes  by viewModel.dishes.collectAsState()
    val drafts  by viewModel.draftItems.collectAsState()

    var selectedCategory by remember { mutableStateOf("Todos") }
    var dishToCustomize  by remember { mutableStateOf<Dishes?>(null) }

    val categories = listOf("Todos") + dishes.map { it.categoryName }.distinct().sortedBy { it }
    val displayed  = if (selectedCategory == "Todos") dishes
                     else dishes.filter { it.categoryName == selectedCategory }

    Row(modifier = Modifier.fillMaxSize()) {
        // ── Left: catalogue ──────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category pills
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val sel = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .background(if (sel) orange else grayBg, RoundedCornerShape(20.dp))
                            .border(1.dp, if (sel) orange else grayBorder, RoundedCornerShape(20.dp))
                            .clickable { selectedCategory = cat!! }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(cat!!, color = if (sel) Color.White else Color(0xFF374151), fontSize = 13.sp)
                    }
                }
            }

            // Dish cards
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(displayed) { dish ->
                    DishCatalogueCard(dish = dish, onClick = { dishToCustomize = dish })
                }
            }
        }

        VerticalDivider()

        // ── Right: order summary ─────────────────────────────────────────
        Column(
            modifier = Modifier
                .width(320.dp)
                .fillMaxHeight()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Pedido actual", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F172A))

            if (drafts.isEmpty()) {
                Box(
                    Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aún no hay platos", color = Color(0xFF94A3B8))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(drafts) { idx, draft ->
                        DraftItemCard(draft = draft, onRemove = { viewModel.removeDraftItem(idx) })
                    }
                }
            }

            HorizontalDivider()
            val total = drafts.sumOf { it.dish.price * it.quantity }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.SemiBold)
                Text("%.2f €".format(total), fontWeight = FontWeight.Bold, color = orange)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = viewModel::backToType,
                    shape   = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text("← Volver") }
                Button(
                    onClick  = viewModel::proceedToPayment,
                    enabled  = drafts.isNotEmpty(),
                    colors   = ButtonDefaults.buttonColors(containerColor = orange),
                    shape    = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) { Text("Pagar →", color = Color.White, fontWeight = FontWeight.SemiBold) }
            }
        }
    }

    // ── Dish customisation dialog ────────────────────────────────────────────
    dishToCustomize?.let { dish ->
        DishCustomizeDialog(
            dish      = dish,
            onDismiss = { dishToCustomize = null },
            onAdd     = { draft ->
                viewModel.addDraftItem(draft)
                dishToCustomize = null
            }
        )
    }
}

@Composable
private fun DishCatalogueCard(dish: Dishes, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(orangeLight, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("🍽", fontSize = 22.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(dish.name, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                Text(dish.description?.take(60) + if ((dish.description?.length ?: 0) > 60) "…" else "",
                    style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
            }
            Text("%.2f €".format(dish.price), fontWeight = FontWeight.Bold, color = orange)
        }
    }
}

@Composable
private fun DraftItemCard(draft: DraftItem, onRemove: () -> Unit) {
    val mods = draft.ingredientMods.entries
        .filter { it.value != "NORMAL" }
        .joinToString(", ") { (id, mod) ->
            val ingName = draft.dish.ingredients.firstOrNull { it.ingredient.id == id }?.ingredient?.name ?: "Ing $id"
            if (mod == "EXTRA") "+$ingName" else "-$ingName"
        }
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = grayBg,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .background(orange, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("x${draft.quantity}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(draft.dish.name, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                }
                if (draft.notes.isNotBlank()) {
                    Text(draft.notes, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                }
                if (mods.isNotBlank()) {
                    Text(mods, style = MaterialTheme.typography.bodySmall, color = orange)
                }
            }
            Text("%.2f €".format(draft.dish.price * draft.quantity), fontWeight = FontWeight.Bold, color = orange)
            Spacer(Modifier.width(8.dp))
            TextButton(onClick = onRemove, contentPadding = PaddingValues(0.dp)) {
                Text("✕", color = Color(0xFFEF4444), fontSize = 16.sp)
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Dish customisation dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun DishCustomizeDialog(
    dish: Dishes,
    onDismiss: () -> Unit,
    onAdd: (DraftItem) -> Unit
) {
    var notes     by remember { mutableStateOf("") }
    // ingredient id -> "NORMAL" | "REMOVE" | "EXTRA"
    val mods = remember {
        mutableStateMapOf<Int, String>().also { map ->
            dish.ingredients.forEach { map[it.ingredient.id] = "NORMAL" }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = Color.White,
        shape            = RoundedCornerShape(16.dp),
        title = {
            Column {
                Text(dish.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text("%.2f € / unidad".format(dish.price),
                    style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .widthIn(min = 420.dp, max = 560.dp)
                    .heightIn(max = 520.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Notes
                OutlinedTextField(
                    value         = notes,
                    onValueChange = { notes = it },
                    label         = { Text("Notas (opcional)") },
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(8.dp),
                    singleLine    = true
                )

                // Ingredients
                if (dish.ingredients.isNotEmpty()) {
                    Text("Ingredientes", fontWeight = FontWeight.SemiBold)
                    dish.ingredients.forEach { di ->
                        val id  = di.ingredient.id
                        val mod = mods[id] ?: "NORMAL"
                        IngredientModRow(
                            di  = di,
                            mod = mod,
                            onChange = { mods[id] = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAdd(DraftItem(dish = dish, quantity = 1, notes = notes, ingredientMods = mods.toMap()))
                },
                colors = ButtonDefaults.buttonColors(containerColor = orange),
                shape  = RoundedCornerShape(10.dp)
            ) {
                Text("Añadir al pedido", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = Color(0xFF64748B)) }
        }
    )
}

@Composable
private fun IngredientModRow(
    di: DishIngredient,
    mod: String,
    onChange: (String) -> Unit
) {
    val options = listOf("REMOVE" to "Quitar", "NORMAL" to "Normal", "EXTRA" to "Extra")
    Row(
        modifier              = Modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "${di.ingredient.name}  (${di.quantity} ${di.ingredient.unit})",
            modifier = Modifier.weight(1f),
            style    = MaterialTheme.typography.bodyMedium,
            color    = Color(0xFF374151)
        )
        Spacer(Modifier.width(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            options.forEach { (value, label) ->
                val sel = mod == value
                val bg  = when {
                    sel && value == "EXTRA"  -> Color(0xFFDCFCE7)
                    sel && value == "REMOVE" -> Color(0xFFFFEBEE)
                    sel                      -> orangeLight
                    else                     -> Color(0xFFF1F5F9)
                }
                val textColor = when {
                    sel && value == "EXTRA"  -> Color(0xFF16A34A)
                    sel && value == "REMOVE" -> Color(0xFFEF4444)
                    sel                      -> orange
                    else                     -> Color(0xFF64748B)
                }
                Box(
                    modifier = Modifier
                        .background(bg, RoundedCornerShape(6.dp))
                        .border(1.dp, if (sel) textColor else Color(0xFFE2E8F0), RoundedCornerShape(6.dp))
                        .clickable { onChange(value) }
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(label, color = textColor, fontSize = 12.sp, fontWeight = if (sel) FontWeight.SemiBold else FontWeight.Normal)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Step 3 — Payment
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StepPaymentScreen(viewModel: NewOrderViewModel) {
    val drafts       by viewModel.draftItems.collectAsState()
    val orderType    by viewModel.orderType.collectAsState()
    val selectedTable by viewModel.selectedTableId.collectAsState()

    var paymentMethod by remember { mutableStateOf("CARD") } // "CARD" | "CASH"
    var cashGivenText by remember { mutableStateOf("") }
    var submitting    by remember { mutableStateOf(false) }

    val subTotal = drafts.sumOf { it.dish.price * it.quantity }
    val cashGiven = cashGivenText.toDoubleOrNull()
    val change    = if (paymentMethod == "CASH" && cashGiven != null) cashGiven - subTotal else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // ── Order summary ─────────────────────────────────────────────────
        Surface(shape = RoundedCornerShape(14.dp), shadowElevation = 2.dp, color = Color.White) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Resumen del pedido", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text(
                    when (orderType) {
                        OrderType.TABLE    -> "Mesa ${selectedTable ?: "—"}"
                        OrderType.TAKEAWAY -> "Para llevar"
                        OrderType.PICKUP   -> "Recoger en local"
                        OrderType.DELIVERY -> "A domicilio"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B)
                )
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                drafts.forEach { draft ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${draft.quantity}× ${draft.dish.name}", style = MaterialTheme.typography.bodyMedium)
                        Text("%.2f €".format(draft.dish.price * draft.quantity), fontWeight = FontWeight.Medium, color = orange)
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("TOTAL", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("%.2f €".format(subTotal), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = orange)
                }
            }
        }

        // ── Payment method ────────────────────────────────────────────────
        Text("Método de pago", fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = Color(0xFF0F172A))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            listOf("CARD" to "💳  Tarjeta", "CASH" to "💵  Efectivo").forEach { (method, label) ->
                val sel = paymentMethod == method
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (sel) orange else grayBg,
                    shadowElevation = if (sel) 4.dp else 1.dp,
                    modifier = Modifier.height(52.dp).clickable { paymentMethod = method }
                ) {
                    Box(Modifier.padding(horizontal = 28.dp), contentAlignment = Alignment.Center) {
                        Text(label, fontWeight = FontWeight.SemiBold, color = if (sel) Color.White else Color(0xFF374151))
                    }
                }
            }
        }

        // ── Cash sub-panel ────────────────────────────────────────────────
        if (paymentMethod == "CASH") {
            OutlinedTextField(
                value         = cashGivenText,
                onValueChange = { cashGivenText = it },
                label         = { Text("Importe entregado (€)") },
                shape         = RoundedCornerShape(10.dp),
                modifier      = Modifier.width(260.dp)
            )
            if (change != null) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (change >= 0) Color(0xFFDCFCE7) else Color(0xFFFFEBEE)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(if (change >= 0) "Cambio:" else "Importe insuficiente:",
                            fontWeight = FontWeight.SemiBold,
                            color = if (change >= 0) Color(0xFF16A34A) else Color(0xFFEF4444))
                        Text(
                            "%.2f €".format(if (change >= 0) change else -change),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize   = 20.sp,
                            color      = if (change >= 0) Color(0xFF16A34A) else Color(0xFFEF4444)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // ── Action buttons ────────────────────────────────────────────────
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick  = viewModel::backToDishes,
                shape    = RoundedCornerShape(10.dp),
                modifier = Modifier.height(52.dp)
            ) { Text("← Volver") }

            val canPay = paymentMethod == "CARD" || (cashGiven != null && cashGiven >= subTotal)
            Button(
                onClick  = {
                    if (submitting) return@Button
                    submitting = true
                    viewModel.submitOrder(paymentMethod, cashGiven) { _ ->
                        submitting = false
                    }
                },
                enabled  = canPay && !submitting,
                colors   = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                shape    = RoundedCornerShape(10.dp),
                modifier = Modifier.height(52.dp).weight(1f)
            ) {
                Text(
                    if (submitting) "Enviando…" else "✔  Confirmar pedido",
                    color      = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 16.sp
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun StepDot(dotStep: Int, currentStep: Int) {
    val active = dotStep == currentStep
    val done   = dotStep < currentStep
    Box(
        modifier = Modifier
            .size(if (active) 32.dp else 26.dp)
            .background(
                color = when { active -> orange; done -> Color(0xFF16A34A); else -> Color(0xFFE2E8F0) },
                shape = RoundedCornerShape(50)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text  = if (done) "✓" else dotStep.toString(),
            color = if (dotStep <= currentStep) Color.White else Color(0xFF94A3B8),
            fontWeight = FontWeight.Bold,
            fontSize   = 13.sp
        )
    }
}
