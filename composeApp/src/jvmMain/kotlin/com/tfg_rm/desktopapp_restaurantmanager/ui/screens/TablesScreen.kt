package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Table
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.TablesViewModel
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlin.math.roundToInt

// ─── constants ───────────────────────────────────────────────────────────────
private val tableOrange  = Color(0xFFFF6A00)
private val tableOrangeD = Color(0xFFCC5500)
private val cellSize     = 120.dp
private val cellGap      = 16.dp
private const val GRID_COLS = 6
private const val GRID_ROWS = 5

// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun TablesScreen(viewModel: TablesViewModel, modifier: Modifier = Modifier) {
    val tables by viewModel.tables.collectAsState()

    // Table being configured (capacity dialog)
    var configTarget by remember { mutableStateOf<Table?>(null) }
    // Table being dragged from the grid (id -> current drag offset from its origin)
    var draggingId     by remember { mutableStateOf<Int?>(null) }
    var dragOffset     by remember { mutableStateOf(Offset.Zero) }
    // Pending new-table drop: grid position chosen during drag from sidebar
    var pendingNewCol  by remember { mutableStateOf<Int?>(null) }
    var pendingNewRow  by remember { mutableStateOf<Int?>(null) }

    // Sidebar "Nueva Mesa" drag state
    var newTableDragging by remember { mutableStateOf(false) }
    var newTableOffset   by remember { mutableStateOf(Offset.Zero) }
    // Absolute screen position of the sidebar chip (tracked via onGloballyPositioned)
    var chipAbsPos       by remember { mutableStateOf(Offset.Zero) }
    // We need the absolute position of the grid area to convert drag position → cell
    var gridTopLeft      by remember { mutableStateOf(Offset.Zero) }
    // Top-left of this composable in root coords (for ghost overlay positioning)
    var screenOrigin     by remember { mutableStateOf(Offset.Zero) }

    val totalCapacity = tables.sumOf { it.capacity }

    // Atributes to observe the place to drop the tables
    var hoverCol by remember { mutableStateOf<Int?>(null) }
    var hoverRow by remember { mutableStateOf<Int?>(null) }
    var dragStartBase by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { coords -> screenOrigin = coords.positionInRoot() }
    ) {
    Row(modifier = Modifier.fillMaxSize()) {

        // ── Left sidebar ─────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Add-table card
            Surface(
                shape         = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp,
                modifier      = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        Strings.t("screen.tables.add_title"),
                        fontWeight = FontWeight.Bold,
                        style      = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        Strings.t("screen.tables.add_hint"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(12.dp))

                    // Draggable "Nueva Mesa" chip
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.CenterHorizontally)
                            .onGloballyPositioned { coords ->
                                chipAbsPos = coords.positionInRoot()
                            }
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { localOffset ->
                                        newTableDragging = true
                                        // chipAbsPos + localOffset = absolute cursor position on screen
                                        newTableOffset   = chipAbsPos + localOffset

                                        hoverCol = null
                                        hoverRow = null
                                    },
                                    onDrag = { change, amount ->
                                        change.consume()
                                        newTableOffset += amount

                                        val relX = newTableOffset.x - gridTopLeft.x + cellSize.toPx() / 2
                                        val relY = newTableOffset.y - gridTopLeft.y + cellSize.toPx() / 2
                                        val cellPx = (cellSize + cellGap).toPx()

                                        val col = (relX / cellPx).toInt() + 1
                                        val row = (relY / cellPx).toInt() + 1

                                        hoverCol = col.coerceIn(1, GRID_COLS)
                                        hoverRow = row.coerceIn(1, GRID_ROWS)
                                    },
                                    onDragEnd = {
                                        newTableDragging = false

                                        if (hoverCol != null && hoverRow != null) {
                                            pendingNewCol = hoverCol
                                            pendingNewRow = hoverRow
                                        }

                                        hoverCol = null
                                        hoverRow = null
                                        newTableOffset = Offset.Zero
                                    },
                                    onDragCancel = {
                                        newTableDragging = false
                                        newTableOffset   = Offset.Zero
                                    }
                                )
                            }
                    ) {
                        Surface(
                            shape           = RoundedCornerShape(12.dp),
                            color           = Color(0xFFF3F4F6),
                            shadowElevation = if (newTableDragging) 8.dp else 2.dp,
                            modifier        = Modifier.fillMaxSize()
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier            = Modifier.fillMaxSize()
                            ) {
                                Text("⬛", fontSize = 22.sp)
                                Text(
                                    Strings.t("screen.tables.new_table"),
                                    style     = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // Summary card
            Surface(
                shape           = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp,
                modifier        = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(Strings.t("screen.tables.summary_title"), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(Strings.t("screen.tables.stat.total"),    style = MaterialTheme.typography.bodyMedium)
                        Text(tables.size.toString(),                    style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(Strings.t("screen.tables.stat.capacity"), style = MaterialTheme.typography.bodyMedium)
                        Text(
                            String.format(Strings.t("screen.tables.stat.capacity_value"), totalCapacity),
                            style      = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Help card
            Surface(
                shape           = RoundedCornerShape(16.dp),
                color           = Color(0xFFFFF2E6),
                shadowElevation = 0.dp,
                modifier        = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("⚙", fontSize = 16.sp, color = tableOrange)
                        Text(Strings.t("screen.tables.help.title"), fontWeight = FontWeight.Bold, color = tableOrange)
                    }
                    listOf(
                        Strings.t("screen.tables.help.drag_existing"),
                        Strings.t("screen.tables.help.click_config"),
                        Strings.t("screen.tables.help.drag_new")
                    ).forEach { hint ->
                        Text("• $hint", style = MaterialTheme.typography.bodySmall, color = tableOrangeD)
                    }
                }
            }
        }

        // ── Grid area ────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Surface(
                modifier        = Modifier.fillMaxSize(),
                shape           = RoundedCornerShape(16.dp),
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text(
                        Strings.t("screen.tables.grid.hint"),
                        style  = MaterialTheme.typography.bodySmall,
                        color  = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Grid
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { coords ->
                                gridTopLeft = coords.positionInRoot()
                            }
                    ) {
                        // Empty cell drop zones
                        for (r in 1..GRID_ROWS) {
                            for (c in 1..GRID_COLS) {
                                val isHover = (hoverCol == c && hoverRow == r)
                                val occupied = tables.any { it.posX == c && it.posY == r }
                                if (!occupied) {
                                    Box(
                                        modifier = Modifier
                                            .offset(
                                                x = ((c - 1) * (cellSize + cellGap).value).dp,
                                                y = ((r - 1) * (cellSize + cellGap).value).dp
                                            )
                                            .size(cellSize)
                                            .border(
                                                width = if (isHover) 3.dp else 1.dp,
                                                color = if (isHover) tableOrange else Color(0xFFE2E8F0),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .background(
                                                if (isHover) tableOrange.copy(alpha = 0.15f) else Color.Transparent
                                            )
                                    )
                                }
                            }
                        }

                        // Placed tables
                        tables.forEach { table ->
                            val isDefault  = table.id == 1
                            val isDragging = draggingId == table.id

                            Box(
                                modifier = Modifier
                                    .offset {
                                        val baseX = ((table.posX - 1) * (cellSize + cellGap).toPx()).roundToInt()
                                        val baseY = ((table.posY - 1) * (cellSize + cellGap).toPx()).roundToInt()
                                        if (isDragging) {
                                            IntOffset(
                                                (baseX + dragOffset.x).roundToInt(),
                                                (baseY + dragOffset.y).roundToInt()
                                            )
                                        } else {
                                            IntOffset(baseX, baseY)
                                        }
                                    }
                                    .size(cellSize)
                                    .shadow(if (isDragging) 12.dp else 4.dp, RoundedCornerShape(14.dp))
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(tableOrange)
                                    // Long-press drag (non-default only)
                                    .then(
                                        if (!isDefault) Modifier.pointerInput(table.id) {
                                            detectDragGestures(
                                                onDragStart = {
                                                    draggingId = table.id
                                                    dragOffset = Offset.Zero

                                                    dragStartBase = Offset(
                                                        (table.posX - 1) * (cellSize + cellGap).toPx(),
                                                        (table.posY - 1) * (cellSize + cellGap).toPx()
                                                    )

                                                    hoverCol = null
                                                    hoverRow = null
                                                },
                                                onDrag      = { change, amount ->
                                                    change.consume()
                                                    dragOffset += amount

                                                    val cellPx = (cellSize + cellGap).toPx()

                                                    val absoluteX = dragStartBase.x + dragOffset.x
                                                    val absoluteY = dragStartBase.y + dragOffset.y

                                                    val col = ((absoluteX + cellSize.toPx() / 2) / cellPx).toInt() + 1
                                                    val row = ((absoluteY + cellSize.toPx() / 2) / cellPx).toInt() + 1

                                                    hoverCol = col.coerceIn(1, GRID_COLS)
                                                    hoverRow = row.coerceIn(1, GRID_ROWS)
                                                },
                                                onDragEnd   = {
                                                    if (hoverCol != null && hoverRow != null) {
                                                        val occupied = tables.any { it.posX == hoverCol && it.posY == hoverRow }

                                                        if (!occupied) {
                                                            viewModel.moveTable(table.id, hoverCol!!, hoverRow!!)
                                                        }
                                                    }

                                                    draggingId = null
                                                    dragOffset = Offset.Zero
                                                    hoverCol = null
                                                    hoverRow = null
                                                },
                                                onDragCancel = { draggingId = null; dragOffset = Offset.Zero }
                                            )
                                        } else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        table.id.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize   = 32.sp,
                                        color      = Color.White
                                    )
                                    Row(
                                        verticalAlignment    = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                                    ) {
                                        Text("👤", fontSize = 11.sp, color = Color.White)
                                        Text(
                                            table.capacity.toString(),
                                            fontSize     = 13.sp,
                                            color        = Color.White.copy(alpha = 0.9f),
                                            fontWeight   = FontWeight.Medium
                                        )
                                    }
                                }

                                // Config button (top-right corner)
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(6.dp)
                                ) {
                                    // Invisible hit area — clicking configures the table
                                    Surface(
                                        shape  = RoundedCornerShape(6.dp),
                                        color  = Color.White.copy(alpha = 0.25f),
                                        modifier = Modifier.size(22.dp).clickable(
                                            indication = null,
                                            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                            onClick = { configTarget = table }
                                        )
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text("✎", fontSize = 11.sp, color = Color.White)
                                        }
                                    }
                                }

                                // Delete button (bottom-right, non-default only)
                                if (!isDefault) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .padding(6.dp)
                                    ) {
                                        Surface(
                                            shape    = RoundedCornerShape(6.dp),
                                            color    = Color.White.copy(alpha = 0.25f),
                                            modifier = Modifier.size(22.dp).clickable(
                                                indication = null,
                                                interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
                                                onClick = { viewModel.removeTable(table.id) }
                                            )
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text("✕", fontSize = 11.sp, color = Color.White)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

        // ── Drag ghost (sidebar → grid) ──────────────────────────────────────
        if (newTableDragging) {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (newTableOffset.x - screenOrigin.x).roundToInt() - (cellSize / 2).roundToPx(),
                            (newTableOffset.y - screenOrigin.y).roundToInt() - (cellSize / 2).roundToPx()
                        )
                    }
                    .size(cellSize)
                    .background(tableOrange.copy(alpha = 0.75f), RoundedCornerShape(14.dp))
                    .shadow(12.dp, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
            }
        }
    } // end outer Box

    // ── Capacity config dialog ────────────────────────────────────────────────
    configTarget?.let { table ->
        TableConfigDialog(
            table     = table,
            onDismiss = { configTarget = null },
            onSave    = { cap -> viewModel.setCapacity(table.id, cap); configTarget = null }
        )
    }

    // ── New table capacity dialog (after drag-drop from sidebar) ─────────────
    if (pendingNewCol != null && pendingNewRow != null) {
        val col = pendingNewCol!!
        val row = pendingNewRow!!
        NewTableDialog(
            col       = col,
            row       = row,
            onDismiss = { pendingNewCol = null; pendingNewRow = null },
            onSave    = { cap ->
                viewModel.addTable(col, row, cap)
                pendingNewCol = null
                pendingNewRow = null
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Capacity configuration dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TableConfigDialog(table: Table, onDismiss: () -> Unit, onSave: (Int) -> Unit) {
    var capacityText by remember { mutableStateOf(table.capacity.toString()) }
    var error        by remember { mutableStateOf("") }
    val isDefault    = table.id == 1

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                String.format(Strings.t("screen.tables.config.title"), table.id),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (isDefault) {
                    Text(
                        Strings.t("screen.tables.config.default_note"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    String.format(Strings.t("screen.tables.config.position"), table.posX, table.posY),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value         = capacityText,
                    onValueChange = { capacityText = it; error = "" },
                    label         = { Text(Strings.t("screen.tables.config.capacity_label")) },
                    isError       = error.isNotEmpty(),
                    supportingText = if (error.isNotEmpty()) {{ Text(error, color = MaterialTheme.colorScheme.error) }} else null,
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cap = capacityText.trim().toIntOrNull()
                    if (cap == null || cap < 1) {
                        error = Strings.t("screen.tables.config.error.capacity_invalid")
                    } else {
                        onSave(cap)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = tableOrange)
            ) { Text(Strings.t("screen.tables.config.save"), color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(Strings.t("screen.tables.config.cancel")) }
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// New-table dialog (fired when user drops the sidebar chip onto the grid)
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun NewTableDialog(col: Int, row: Int, onDismiss: () -> Unit, onSave: (Int) -> Unit) {
    var capacityText by remember { mutableStateOf("4") }
    var error        by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                String.format(Strings.t("screen.tables.new_dialog.title"), col, row),
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            OutlinedTextField(
                value         = capacityText,
                onValueChange = { capacityText = it; error = "" },
                label         = { Text(Strings.t("screen.tables.config.capacity_label")) },
                isError       = error.isNotEmpty(),
                supportingText = if (error.isNotEmpty()) {{ Text(error, color = MaterialTheme.colorScheme.error) }} else null,
                singleLine    = true,
                modifier      = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val cap = capacityText.trim().toIntOrNull()
                    if (cap == null || cap < 1) {
                        error = Strings.t("screen.tables.config.error.capacity_invalid")
                    } else {
                        onSave(cap)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = tableOrange)
            ) { Text(Strings.t("screen.tables.config.save"), color = Color.White) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(Strings.t("screen.tables.config.cancel")) }
        }
    )
}

