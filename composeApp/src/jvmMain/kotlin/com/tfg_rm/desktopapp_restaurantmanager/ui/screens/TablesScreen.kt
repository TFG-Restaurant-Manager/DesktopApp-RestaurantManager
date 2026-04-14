package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
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
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.components.UiState
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlin.math.roundToInt

// ─── constants ───────────────────────────────────────────────────────────────
private val tableOrange = Color(0xFFFF6A00)
private val tableOrangeD = Color(0xFFCC5500)
private val cellSize = 120.dp
private val cellGap = 16.dp

// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun TablesScreen(viewModel: TablesViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.tables.collectAsState()

    when (state) {
        is UiState.Idle -> {
            viewModel.loadTables()
        }

        is UiState.Error -> {
            ErrorScreen(
                title = Strings.t("screen.tables.error.title"),
                message = (state as UiState.Error).message,
                primaryAction = Pair(
                    Strings.t("reload"),
                    { viewModel.loadTables() }
                )
            )
        }

        is UiState.Loading -> {
            LoadingScreen(Strings.t("screen.tables.loading.message"))
        }

        is UiState.Success<List<Table>> -> {
            val tablesState = (state as UiState.Success<List<Table>>).data
            // Atributes to observe the section
            var actualSection by remember { mutableStateOf(tablesState.getOrNull(0)?.section ?: "Sec1") }
            val sections = viewModel.sections.collectAsState()
            var showNewSectionDialog by remember { mutableStateOf(false) }

            val tables = tablesState.filter { it.section == actualSection }

            var maxX by remember { mutableStateOf(tables.maxOfOrNull { it.posX }) }
            var maxY by remember { mutableStateOf(tables.maxOfOrNull { it.posY }) }

            var gridColumns by remember { mutableStateOf(if (maxX == null || maxX!! < 2) 2 else maxX!!) }
            var gridRows by remember { mutableStateOf(if (maxY == null || maxY!! < 2) 2 else maxY!!) }

            // Table being configured (capacity dialog)
            var configTarget by remember { mutableStateOf<Table?>(null) }
            // Table being dragged from the grid (id -> current drag offset from its origin)
            var draggingId by remember { mutableStateOf<Int?>(null) }
            var dragOffset by remember { mutableStateOf(Offset.Zero) }
            // Pending new-table drop: grid position chosen during drag from sidebar
            var pendingNewCol by remember { mutableStateOf<Int?>(null) }
            var pendingNewRow by remember { mutableStateOf<Int?>(null) }

            // Sidebar "Nueva Mesa" drag state
            var newTableDragging by remember { mutableStateOf(false) }
            var newTableOffset by remember { mutableStateOf(Offset.Zero) }
            // Absolute screen position of the sidebar chip (tracked via onGloballyPositioned)
            var chipAbsPos by remember { mutableStateOf(Offset.Zero) }
            // We need the absolute position of the grid area to convert drag position → cell
            var gridTopLeft by remember { mutableStateOf(Offset.Zero) }
            // Top-left of this composable in root coords (for ghost overlay positioning)
            var screenOrigin by remember { mutableStateOf(Offset.Zero) }

            val totalCapacity = tables.sumOf { it.capacity }

            // Atributes to observe the place to drop the tables
            var hoverCol by remember { mutableStateOf<Int?>(null) }
            var hoverRow by remember { mutableStateOf<Int?>(null) }
            var dragStartBase by remember { mutableStateOf(Offset.Zero) }

            LaunchedEffect(actualSection, tablesState) {
                // Filtramos las mesas de la sección actual
                val tablesInSection = tablesState.filter { it.section == actualSection }

//        if (tablesInSection.isEmpty()) {
//            actualSection = tablesState.firstOrNull()?.section ?: "---"
//            tablesInSection = tablesState.filter { it.section == actualSection }
//        }

                val maxX = tablesInSection.maxOfOrNull { it.posX } ?: 0
                val maxY = tablesInSection.maxOfOrNull { it.posY } ?: 0

                // Actualizamos el estado del grid (mínimo 2x2 para que no se vea vacío)
                gridColumns = if (maxX < 2) 2 else maxX
                gridRows = if (maxY < 2) 2 else maxY
            }

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
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Add-table card
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    Strings.t("screen.tables.add_title"),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
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
                                                    newTableOffset = chipAbsPos + localOffset

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

                                                    hoverCol = col.coerceIn(1, gridColumns)
                                                    hoverRow = row.coerceIn(1, gridRows)
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
                                                    newTableOffset = Offset.Zero
                                                }
                                            )
                                        }
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFF3F4F6),
                                        shadowElevation = if (newTableDragging) 8.dp else 2.dp,
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center,
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Text("⬛", fontSize = 22.sp)
                                            Text(
                                                Strings.t("screen.tables.new_table"),
                                                style = MaterialTheme.typography.labelSmall,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Summary card
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    Strings.t("screen.tables.summary_title"),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(
                                        Strings.t("screen.tables.stat.total"),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        tables.size.toString(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(
                                        Strings.t("screen.tables.stat.capacity"),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        String.format(Strings.t("screen.tables.stat.capacity_value"), totalCapacity),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Help card
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFFFF2E6),
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text("⚙", fontSize = 16.sp, color = tableOrange)
                                    Text(
                                        Strings.t("screen.tables.help.title"),
                                        fontWeight = FontWeight.Bold,
                                        color = tableOrange
                                    )
                                }
                                listOf(
                                    Strings.t("screen.tables.help.drag_existing"),
                                    Strings.t("screen.tables.help.click_config"),
                                    Strings.t("screen.tables.help.click_eliminate"),
                                    Strings.t("screen.tables.help.drag_new")
                                ).forEach { hint ->
                                    Text("• $hint", style = MaterialTheme.typography.bodySmall, color = tableOrangeD)
                                }
                            }
                        }

                        // Sections selector
                        var expanded by remember { mutableStateOf(false) }
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            shadowElevation = 2.dp,
                            modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(Strings.t("screen.tables.section.title"))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = actualSection,
                                        onValueChange = {},
                                        modifier = Modifier.fillMaxWidth(),
                                        label = { Text(Strings.t("screen.tables.section.label")) },
                                        readOnly = true,
                                        enabled = true,
                                        singleLine = true,
                                        isError = false,
                                        textStyle = LocalTextStyle.current,
                                        trailingIcon = {
                                            Icon(
                                                imageVector = if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                                contentDescription = null
                                            )
                                        }
                                    )

                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .clickable { expanded = !expanded }
                                    )
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        modifier = Modifier.fillMaxWidth(0.9f)
                                    ) {
                                        if (sections.value.isNotEmpty()) {
                                            sections.value.forEach { option ->
                                                DropdownMenuItem(
                                                    text = { Text(option) },
                                                    onClick = {
                                                        actualSection = option
                                                        expanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                                Button(
                                    onClick = { showNewSectionDialog = true },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = tableOrange,
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = Strings.t("screen.tables.section.emptysections"),
                                        fontSize = MaterialTheme.typography.bodyMedium.fontSize
                                    )
                                }
                            }
                        }
                    }
                    val scrollStateVertical = rememberScrollState()
                    val scrollStateHorizontal = rememberScrollState()

// ── Grid area ───────────────────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        if (actualSection != "---") {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = RoundedCornerShape(16.dp),
                                shadowElevation = 2.dp
                            ) {
                                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                                    // Fila de botones de control (Naranja Pastel)
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.padding(bottom = 12.dp)
                                            .horizontalScroll(rememberScrollState())
                                    ) {
                                        Text(
                                            text = Strings.t("screen.tables.grid.hint"),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(end = 8.dp)
                                        )

                                        @Composable
                                        fun GridControlButton(text: String, onClick: () -> Unit) {
                                            Button(
                                                onClick = onClick,
                                                modifier = Modifier.height(32.dp),
                                                shape = RoundedCornerShape(8.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFFFFF2E6),
                                                    contentColor = tableOrange
                                                ),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                                            ) {
                                                Text(
                                                    text = text,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }

                                        GridControlButton(Strings.t("screen.tables.grid.addcolumn")) { gridColumns++ }
                                        GridControlButton(Strings.t("screen.tables.grid.addrow")) { gridRows++ }
                                        GridControlButton(Strings.t("screen.tables.grid.minuscolumn")) {
                                            if (gridColumns > 2) {
                                                tables.filter {
                                                    it.posX == gridColumns
                                                }.forEach {
                                                    viewModel.removeTable(it.id)
                                                }
                                                gridColumns--
                                            }
                                        }
                                        GridControlButton(Strings.t("screen.tables.grid.minusrow")) {
                                            if (gridRows > 2) {
                                                tables.filter {
                                                    it.posY == gridRows
                                                }.forEach {
                                                    viewModel.removeTable(it.id)
                                                }
                                                gridRows--
                                            }
                                        }
                                    }

                                    // Área de Grid con Scrollbars
                                    Box(modifier = Modifier.fillMaxSize()) {
                                        // Contenedor con scroll
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(end = 12.dp, bottom = 12.dp) // Espacio para las barras
                                                .horizontalScroll(scrollStateHorizontal)
                                                .verticalScroll(scrollStateVertical)
                                        ) {
                                            // El Grid real con tamaño calculado según filas/columnas
                                            Box(
                                                modifier = Modifier
                                                    .size(
                                                        width = (gridColumns * (cellSize.value + cellGap.value)).dp + 32.dp,
                                                        height = (gridRows * (cellSize.value + cellGap.value)).dp + 32.dp
                                                    )
                                                    .onGloballyPositioned { coords ->
                                                        gridTopLeft = coords.positionInRoot()
                                                    }
                                            ) {
                                                // Zonas de caída (Empty cells)
                                                for (r in 1..gridRows) {
                                                    for (c in 1..gridColumns) {
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
                                                                        color = if (isHover) tableOrange else Color(
                                                                            0xFFE2E8F0
                                                                        ),
                                                                        shape = RoundedCornerShape(12.dp)
                                                                    )
                                                            )
                                                        }
                                                    }
                                                }

                                                // Mesas colocadas
                                                tables.forEach { table ->
                                                    val isDragging = draggingId == table.id

                                                    Box(
                                                        modifier = Modifier
                                                            .offset {
                                                                val baseX =
                                                                    ((table.posX - 1) * (cellSize + cellGap).toPx()).roundToInt()
                                                                val baseY =
                                                                    ((table.posY - 1) * (cellSize + cellGap).toPx()).roundToInt()
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
                                                            .shadow(
                                                                if (isDragging) 12.dp else 4.dp,
                                                                RoundedCornerShape(14.dp)
                                                            )
                                                            .clip(RoundedCornerShape(14.dp))
                                                            .background(tableOrange)
                                                            .then(
                                                                Modifier.pointerInput(table.id) {
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
                                                                        onDrag = { change, amount ->
                                                                            change.consume()
                                                                            dragOffset += amount
                                                                            val cellPx = (cellSize + cellGap).toPx()
                                                                            val absoluteX =
                                                                                dragStartBase.x + dragOffset.x
                                                                            val absoluteY =
                                                                                dragStartBase.y + dragOffset.y
                                                                            val col =
                                                                                ((absoluteX + cellSize.toPx() / 2) / cellPx).toInt() + 1
                                                                            val row =
                                                                                ((absoluteY + cellSize.toPx() / 2) / cellPx).toInt() + 1
                                                                            hoverCol = col.coerceIn(1, gridColumns)
                                                                            hoverRow = row.coerceIn(1, gridRows)
                                                                        },
                                                                        onDragEnd = {
                                                                            if (hoverCol != null && hoverRow != null) {
                                                                                val occupied =
                                                                                    tables.any { it.posX == hoverCol && it.posY == hoverRow }
                                                                                if (!occupied) {
                                                                                    viewModel.moveTable(
                                                                                        table.id,
                                                                                        hoverCol!!,
                                                                                        hoverRow!!,
                                                                                        actualSection
                                                                                    )
                                                                                }
                                                                            }
                                                                            draggingId = null
                                                                            hoverCol = null
                                                                            hoverRow = null
                                                                        },
                                                                        onDragCancel = { draggingId = null }
                                                                    )
                                                                }
                                                            ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        // Contenido de la mesa (ID y Capacidad)
                                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                            Text(
                                                                table.id.toString(),
                                                                fontWeight = FontWeight.Bold,
                                                                fontSize = 32.sp,
                                                                color = Color.White
                                                            )
                                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                                Text("👤", fontSize = 11.sp, color = Color.White)
                                                                Text(
                                                                    table.capacity.toString(),
                                                                    fontSize = 13.sp,
                                                                    color = Color.White.copy(alpha = 0.9f)
                                                                )
                                                            }
                                                        }
                                                        // --- Botón Editar (Arriba Derecha) ---
                                                        IconButton(
                                                            onClick = { configTarget = table },
                                                            modifier = Modifier
                                                                .align(Alignment.TopEnd)
                                                                .padding(6.dp)
                                                                .size(30.dp)
                                                                .background(
                                                                    color = tableOrange,
                                                                    shape = RoundedCornerShape(6.dp)
                                                                )
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Edit,
                                                                contentDescription = "Editar",
                                                                tint = Color.White,
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }

                                                        // --- Botón Eliminar (Abajo Derecha) ---
                                                        IconButton(
                                                            onClick = { viewModel.removeTable(table.id) },
                                                            modifier = Modifier
                                                                .align(Alignment.BottomEnd)
                                                                .padding(6.dp)
                                                                .size(30.dp)
                                                                .background(
                                                                    color = tableOrange,
                                                                    shape = RoundedCornerShape(6.dp)
                                                                )
                                                        ) {
                                                            Icon(
                                                                imageVector = Icons.Default.Close,
                                                                contentDescription = "Eliminar",
                                                                tint = Color.White,
                                                                modifier = Modifier.size(16.dp)
                                                            )
                                                        }

                                                    }
                                                }
                                            }
                                        }

                                        // Barras de scroll (Visibles en Desktop)
                                        VerticalScrollbar(
                                            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                                            adapter = rememberScrollbarAdapter(scrollStateVertical)
                                        )
                                        HorizontalScrollbar(
                                            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                                            adapter = rememberScrollbarAdapter(scrollStateHorizontal)
                                        )
                                    }
                                }
                            }
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(Strings.t("screen.tables.info.nosections"), color = Color.Red)
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
                            .background(tableOrange, RoundedCornerShape(14.dp))
                        /*.shadow(12.dp, RoundedCornerShape(14.dp))*/,
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (1..tables.map { it.id }.size + 1).first { it !in tables.map { it.id }.toSet() }
                                .toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp,
                            color = Color.White
                        )
                    }
                }
            } // end outer Box

            // ── Capacity config dialog ────────────────────────────────────────────────
            configTarget?.let { table ->
                TableConfigDialog(
                    table = table,
                    onDismiss = { configTarget = null },
                    onSave = { cap -> viewModel.setCapacity(table.id, cap); configTarget = null }
                )
            }

            // ── New table capacity dialog (after drag-drop from sidebar) ─────────────
            if (pendingNewCol != null && pendingNewRow != null) {
                val col = pendingNewCol!!
                val row = pendingNewRow!!
                NewTableDialog(
                    col = col,
                    row = row,
                    onDismiss = { pendingNewCol = null; pendingNewRow = null },
                    onSave = { cap ->
                        viewModel.addTable(col, row, cap, actualSection)
                        pendingNewCol = null
                        pendingNewRow = null
                    }
                )
            }

            if (showNewSectionDialog) {
                NewSectionDialog(
                    onDismiss = { showNewSectionDialog = false },
                    onCreate = { newSection ->
                        viewModel.addSection(newSection)
                        actualSection = newSection
                        showNewSectionDialog = false
                    }
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Capacity configuration dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun TableConfigDialog(table: Table, onDismiss: () -> Unit, onSave: (Int) -> Unit) {
    var capacityText by remember { mutableStateOf(table.capacity.toString()) }
    var error by remember { mutableStateOf("") }

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
                Text(
                    String.format(Strings.t("screen.tables.config.position"), table.posX, table.posY),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = capacityText,
                    onValueChange = { capacityText = it; error = "" },
                    label = { Text(Strings.t("screen.tables.config.capacity_label")) },
                    isError = error.isNotEmpty(),
                    supportingText = if (error.isNotEmpty()) {
                        { Text(error, color = MaterialTheme.colorScheme.error) }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
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
    var error by remember { mutableStateOf("") }

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
                value = capacityText,
                onValueChange = { capacityText = it; error = "" },
                label = { Text(Strings.t("screen.tables.config.capacity_label")) },
                isError = error.isNotEmpty(),
                supportingText = if (error.isNotEmpty()) {
                    { Text(error, color = MaterialTheme.colorScheme.error) }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
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

@Composable
fun NewSectionDialog(
    onDismiss: () -> Unit,
    onCreate: (String) -> Unit
) {
    var sectionName by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Nueva sección",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = sectionName,
                    onValueChange = {
                        sectionName = it
                        error = ""
                    },
                    label = { Text("Nombre de la sección") },
                    singleLine = true,
                    isError = error.isNotEmpty(),
                    supportingText = if (error.isNotEmpty()) {
                        { Text(error, color = MaterialTheme.colorScheme.error) }
                    } else null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val name = sectionName.trim()

                    if (name.isEmpty()) {
                        error = "El nombre no puede estar vacío"
                    } else {
                        onCreate(name)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = tableOrange)
            ) {
                Text("Crear", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

