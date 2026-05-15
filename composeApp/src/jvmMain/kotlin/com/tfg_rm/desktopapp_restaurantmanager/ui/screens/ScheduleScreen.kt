package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.ClipboardShift
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Shift
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.EmployeesViewModel
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.components.UiState
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*

private val DAYS = listOf(
    DayOfWeek.MONDAY,
    DayOfWeek.TUESDAY,
    DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY,
    DayOfWeek.FRIDAY,
    DayOfWeek.SATURDAY,
    DayOfWeek.SUNDAY
)

private val dayNames = mapOf(
    DayOfWeek.MONDAY to Strings.t("day.monday"),
    DayOfWeek.TUESDAY to Strings.t("day.tuesday"),
    DayOfWeek.WEDNESDAY to Strings.t("day.wednesday"),
    DayOfWeek.THURSDAY to Strings.t("day.thursday"),
    DayOfWeek.FRIDAY to Strings.t("day.friday"),
    DayOfWeek.SATURDAY to Strings.t("day.saturday"),
    DayOfWeek.SUNDAY to Strings.t("day.sunday")
)

@Composable
fun ScheduleScreen(viewModel: EmployeesViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.employees.collectAsState()

    if (viewModel.loadRole() != "MANAGER") {
        ErrorScreen(
            title = Strings.t("screen.tables.error.title"),
            message = Strings.t("errors.permission")
        )
    } else {
        when (state) {
            is UiState.Idle -> {
                viewModel.loadEmployees()
            }

            is UiState.Error -> {
                ErrorScreen(
                    title = Strings.t("screen.shift.error.generic"),
                    message = (state as UiState.Error).message,
                    primaryAction = Pair(Strings.t("reload")) { viewModel.loadEmployees() }
                )
            }

            UiState.Loading -> {
                LoadingScreen(Strings.t("screen.shift.loading.message"))
            }

            is UiState.Success<List<Employee>> -> {
                var weekStart by remember { mutableStateOf(LocalDate.now().with(DayOfWeek.MONDAY)) }
                val employees = (state as UiState.Success<List<Employee>>).data
                val saveState = viewModel.scheduleState.collectAsState()
                // Editing state: a pair of (employee, day) being edited
                var editTarget by remember { mutableStateOf<Pair<Employee, DayOfWeek>?>(null) }

                var clipboardSchedules by remember { mutableStateOf<List<ClipboardShift>?>(null) }

                val weekEnd = weekStart.plusDays(6)
                val fmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("es"))


                val totalHours = employees.sumOf { employee ->
                    employee.schedules.filter {
                        val dia = it.startDateTime.toLocalDate()

                        weekStart.equals(dia.minusDays((dia.dayOfWeek.value - 1).toLong()))
                    }.sumOf {
                        val mins = java.time.Duration.between(it.startDateTime, it.endDateTime).toMinutes()
                        if (mins < 0) 0L else mins
                    }
                } / 60 / employees.size

                Column(modifier = modifier.fillMaxSize().padding(horizontal = 32.dp, vertical = 24.dp)) {

                    // Header card
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Canvas(modifier = Modifier.size(24.dp)) {
                                    drawCircle(color = Color(0xFFF97316), style = Stroke(width = 2.dp.toPx()))
                                    drawLine(
                                        color = Color(0xFFF97316),
                                        start = center,
                                        end = Offset(center.x, center.y - 6.dp.toPx()),
                                        strokeWidth = 2.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                    drawLine(
                                        color = Color(0xFFF97316),
                                        start = center,
                                        end = Offset(center.x + 4.dp.toPx(), center.y),
                                        strokeWidth = 2.dp.toPx(),
                                        cap = StrokeCap.Round
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = Strings.t("screen.schedule.header.current_week"),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    Text(
                                        text = "${weekStart.format(fmt)} - ${weekEnd.format(fmt)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = Strings.t("screen.schedule.header.total_hours"),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = "${totalHours}h",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F172A)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                // Week navigation + save
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = { weekStart = weekStart.minusDays(7) },
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF475569)),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text(text = "←", fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { weekStart = weekStart.plusDays(7) },
                                        shape = RoundedCornerShape(8.dp),
                                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF475569)),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        Text(text = "→", fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = { viewModel.saveSchedules() },
                                        enabled = saveState.value !is UiState.Loading,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(36.dp)
                                    ) {
                                        if (saveState.value is UiState.Loading) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(16.dp),
                                                color = Color.White,
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Text(
                                                text = if (saveState.value is UiState.Success) "✓ Guardado" else "Guardar",
                                                color = Color.White,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                                if (saveState.value is UiState.Error) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = (saveState.value as UiState.Error).message,
                                        color = Color(0xFFEF4444),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Hint banner
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F6FF)),
                        border = BorderStroke(1.dp, Color(0xFFD6E4FF)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "💡", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Row {
                                Text(
                                    text = Strings.t("screen.schedule.hint1"),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF1D4ED8),
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = " ${Strings.t("screen.schedule.hint2")}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF1D4ED8),
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Weekly table
                    WeeklyTable(
                        employees = employees,
                        weekStart = weekStart,
                        clipboardSchedules,
                        {
                            println(it)
                            clipboardSchedules = it
                        },
                        editTarget = { editTarget = it },
                        onPaste = { employee, newWeekSchedules ->
                            val otherWeeksSchedules = employee.schedules.filterNot { shift ->
                                val date = shift.startDateTime.toLocalDate()
                                // Si la fecha está entre weekStart y weekStart + 6 días, es de esta semana
                                !date.isBefore(weekStart) && !date.isAfter(weekStart.plusDays(6))
                            }

                            val schedulesToAdd = newWeekSchedules.filter { shift ->
                                val date = shift.startDateTime.toLocalDate()
                                // Si la fecha está entre weekStart y weekStart + 6 días, es de esta semana
                                !date.isBefore(weekStart) && !date.isAfter(weekStart.plusDays(6))
                            }
                            println(schedulesToAdd)
                            val updatedEmployee = employee.copy(
                                schedules = otherWeeksSchedules + schedulesToAdd
                            )

                            viewModel.updateSchedule(updatedEmployee)
                        }
                    )
                }

                // Shift edit dialog
                editTarget?.let { (emp, day) ->
                    val shiftDate = weekStart.plusDays((day.value - 1).toLong())
                    ShiftEditDialog(
                        employee = emp,
                        day = day,
                        weekStart = weekStart,
                        currentShift = emp.schedules.filter { it.startDateTime.toLocalDate().isEqual(shiftDate) },
                        onDismiss = { editTarget = null },
                        onSave = { employee, schedules ->
                            val updatedEmployee = employee.copy(
                                schedules = employee.schedules
                                    .filterNot { it.startDateTime.toLocalDate().equals(shiftDate) }
                                    .plus(schedules)
                            )

                            viewModel.updateSchedule(updatedEmployee)
                            editTarget = null
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun WeeklyTable(
    employees: List<Employee>,
    weekStart: LocalDate,
    clipboardSchedules: List<ClipboardShift>?,
    onclipboardSchedulesChange: (List<ClipboardShift>) -> Unit,
    editTarget: (Pair<Employee, DayOfWeek>) -> Unit,
    onPaste: (Employee, List<Shift>) -> Unit
) {
    val verticalScroll = rememberScrollState()

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {

        BoxWithConstraints {

            val isWideScreen = maxWidth > 900.dp
            val horizontalScroll = rememberScrollState()

            val tableModifier = if (isWideScreen) {
                Modifier.fillMaxWidth()
            } else {
                Modifier
                    .horizontalScroll(horizontalScroll)
                    .width(900.dp) // ancho mínimo para que no se rompa
            }

            Column(modifier = tableModifier) {

                // HEADER
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings.t("screen.schedule.employee_column"),
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155),
                        modifier = Modifier.weight(if (isWideScreen) 1.5f else 1f)
                    )

                    DAYS.forEach { day ->
                        Text(
                            text = dayNames[day] ?: "",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                // BODY
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(verticalScroll)
                ) {

                    employees.forEach { emp ->

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // EMPLOYEE INFO
                            Column(
                                modifier = Modifier
                                    .then(
                                        if (isWideScreen)
                                            Modifier.weight(1.5f)
                                        else
                                            Modifier.width(180.dp)
                                    )
                                    .padding(end = 16.dp)
                            ) {
                                Text(
                                    text = emp.name,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A),
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = emp.roleName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                                Row {
                                    // BOTÓN COPIAR
                                    IconButton(
                                        onClick = {
                                            onclipboardSchedulesChange(
                                                emp.schedules
                                                    .filter { shift ->
                                                        val date = shift.startDateTime.toLocalDate()
                                                        !date.isBefore(weekStart) && !date.isAfter(weekStart.plusDays(6))
                                                    }
                                                    .map { shift ->
                                                        val date = shift.startDateTime.toLocalDate()

                                                        val dayOffset = ChronoUnit.DAYS.between(weekStart, date)

                                                        ClipboardShift(
                                                            dayOffset = dayOffset,
                                                            startTime = shift.startDateTime.toLocalTime(),
                                                            endTime = shift.endDateTime.toLocalTime()
                                                        )
                                                    }
                                            )
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.ContentCopy,
                                            Strings.t("copy")
                                        )
                                    }

                                    // BOTÓN PEGAR
                                    IconButton(
                                        onClick = {
                                            clipboardSchedules?.let { copied ->

                                                val updatedSchedules = copied.map { clip ->

                                                    val targetDate = weekStart.plusDays(clip.dayOffset)

                                                    Shift(
                                                        id = 0,
                                                        startDateTime = LocalDateTime.of(targetDate, clip.startTime),
                                                        endDateTime = LocalDateTime.of(targetDate, clip.endTime)
                                                    )
                                                }

                                                val finalSchedules = emp.schedules.filterNot {
                                                    val dia = it.startDateTime.toLocalDate()
                                                    !dia.isBefore(weekStart) && !dia.isAfter(weekStart.plusDays(6))
                                                } + updatedSchedules

                                                onPaste(emp, finalSchedules)
                                            }
                                        },
                                        enabled = clipboardSchedules != null,
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.ContentPaste, Strings.t("paste"))
                                    }
                                }
                            }

                            // DAYS
                            DAYS.forEach { day ->

                                val shift = emp.schedules.filter {
                                    it.startDateTime.toLocalDate()
                                        .isEqual(weekStart.plusDays((day.value - 1).toLong()))
                                }

                                Box(
                                    modifier = Modifier
                                        .then(
                                            if (isWideScreen)
                                                Modifier.weight(1f)
                                            else
                                                Modifier.width(140.dp)
                                        )
                                        .padding(horizontal = 8.dp)
                                ) {

                                    Button(
                                        onClick = { editTarget(Pair(emp, day)) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .defaultMinSize(minHeight = 42.dp), // Mínimo 42dp, pero crece si hay mucho texto
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            // Color de fondo dinámico
                                            containerColor = if (shift.isNotEmpty()) Color(0xFFDCFCE7) else Color(
                                                0xFFF1F5F9
                                            ),
                                            // Color del contenido (aunque lo sobreescribimos en el Text)
                                            contentColor = if (shift.isNotEmpty()) Color(0xFF16A34A) else Color(
                                                0xFF94A3B8
                                            )
                                        ),
                                        // Eliminamos el padding interno excesivo de los botones por defecto
                                        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 4.dp),
                                        // Quitamos la elevación para que quede plano en la tabla
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 0.dp,
                                            pressedElevation = 2.dp,
                                            hoveredElevation = 1.dp
                                        )
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            if (shift.isNotEmpty()) {
                                                shift.forEach { s ->
                                                    Text(
                                                        text = "${s.startDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))}-${
                                                            s.endDateTime.format(
                                                                DateTimeFormatter.ofPattern("HH:mm")
                                                            )
                                                        }",
                                                        style = MaterialTheme.typography.bodySmall.copy(
                                                            fontSize = 13.sp, // Texto más grande
                                                            lineHeight = 16.sp
                                                        ),
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF16A34A),
                                                        textAlign = TextAlign.Center
                                                    )
                                                }
                                            } else {
                                                Text(
                                                    text = Strings.t("screen.schedule.day.rest"),
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontSize = 13.sp // Texto más grande
                                                    ),
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = Color(0xFF94A3B8),
                                                    textAlign = TextAlign.Center
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0))
                    }
                }
            }
        }
    }
}

@Composable
private fun ShiftEditDialog(
    employee: Employee,
    day: DayOfWeek,
    weekStart: LocalDate,
    currentShift: List<Shift>,
    onDismiss: () -> Unit,
    onSave: (Employee, List<Shift>) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val shiftDate = weekStart.plusDays((day.value - 1).toLong())

    var schedules by remember {
        mutableStateOf(
            currentShift.ifEmpty {
                listOf(
                    Shift(
                        startDateTime = LocalDateTime.of(shiftDate, LocalTime.of(9, 0)),
                        endDateTime = LocalDateTime.of(shiftDate, LocalTime.of(17, 0))
                    )
                )
            }
        )
    }

    // Estado para el selector manual de hora
    var editingTime by remember { mutableStateOf<Triple<Int, Boolean, LocalTime>?>(null) } // Index, IsStart, Time
    var error by remember { mutableStateOf<String?>(null) }

    // Diálogo Manual de Selección de Hora (Sin APIs experimentales)
    if (editingTime != null) {
        val (index, isStart, time) = editingTime!!
        var tempHour by remember { mutableStateOf(time.hour) }
        var tempMinute by remember { mutableStateOf(time.minute) }

        AlertDialog(
            onDismissRequest = { editingTime = null },
            title = {
                Text(
                    if (isStart) Strings.t("screen.shift.start_label") else Strings.t("screen.shift.end_label"),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            text = {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        WheelNumberPicker(
                            value = tempHour,
                            range = 0..23,
                            modifier = Modifier.weight(1f),
                            onValueChange = { tempHour = it }
                        )
                        Text(
                            ":",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        WheelNumberPicker(
                            value = tempMinute,
                            range = 0..59,
                            modifier = Modifier.weight(1f),
                            onValueChange = { tempMinute = it }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newTime = LocalTime.of(tempHour, tempMinute)
                        schedules = schedules.toMutableList().apply {
                            this[index] = if (isStart) {
                                this[index].copy(startDateTime = LocalDateTime.of(shiftDate, newTime))
                            } else {
                                this[index].copy(endDateTime = LocalDateTime.of(shiftDate, newTime))
                            }
                        }
                        editingTime = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316))
                ) { Text(Strings.t("screen.shift.save")) }
            },
            dismissButton = {
                TextButton(onClick = { editingTime = null }) {
                    Text(Strings.t("screen.shift.cancel"), color = Color.Gray)
                }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text("${employee.name} - ${shiftDate.dayOfMonth}/${shiftDate.monthValue}", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.width(420.dp).verticalScroll(rememberScrollState())) {
                schedules.forEachIndexed { index, shift ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón Hora Inicio
                        TimeButton(
                            label = Strings.t("screen.shift.start_label"),
                            time = shift.startDateTime.format(formatter),
                            onClick = { editingTime = Triple(index, true, shift.startDateTime.toLocalTime()) },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Botón Hora Fin
                        TimeButton(
                            label = Strings.t("screen.shift.end_label"),
                            time = shift.endDateTime.format(formatter),
                            onClick = { editingTime = Triple(index, false, shift.endDateTime.toLocalTime()) },
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = { schedules = schedules.filterIndexed { i, _ -> i != index } }) {
                            Text("✕", color = Color.Red)
                        }
                    }
                }

                // Botón Añadir
                if (schedules.size < 4) {
                    TextButton(onClick = {
                        schedules = schedules + Shift(
                            startDateTime = LocalDateTime.of(shiftDate, LocalTime.of(9, 0)),
                            endDateTime = LocalDateTime.of(shiftDate, LocalTime.of(17, 0))
                        )
                    }) { Text("+ " + Strings.t("screen.shift.add_shift")) }
                }

                error?.let { Text(it, color = Color.Red, style = MaterialTheme.typography.bodySmall) }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val isValid = schedules.all { it.endDateTime.isAfter(it.startDateTime) }
                    if (isValid) onSave(employee, schedules) else error =
                        Strings.t("screen.shift.error.end_after_start")
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316))
            ) { Text(Strings.t("screen.shift.save")) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(Strings.t("screen.shift.cancel")) }
        }
    )
}

@Composable
fun TimeButton(label: String, time: String, onClick: () -> Unit, modifier: Modifier) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            Text(time, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun WheelNumberPicker(
    value: Int,
    range: IntRange,
    modifier: Modifier = Modifier,
    onValueChange: (Int) -> Unit
) {
    val itemHeight = 40.dp
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = value - range.first)

    // Sincronizar el scroll con la selección
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val centerIndex = listState.firstVisibleItemIndex +
                    if (listState.firstVisibleItemScrollOffset > 20) 1 else 0
            onValueChange(range.elementAt(centerIndex.coerceIn(0, range.count() - 1)))
            listState.animateScrollToItem(centerIndex)
        }
    }

    Box(modifier = modifier.height(itemHeight * 3), contentAlignment = Alignment.Center) {
        // Fondo resaltado para el item seleccionado
        Surface(
            modifier = Modifier.fillMaxWidth().height(itemHeight),
            color = Color(0xFFF1F5F9),
            shape = RoundedCornerShape(8.dp)
        ) {}

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = itemHeight) // Para que el primero/último lleguen al centro
        ) {
            items(range.toList()) { number ->
                val isSelected = number == value
                Box(
                    modifier = Modifier.height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = number.toString().padStart(2, '0'),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Color(0xFFF97316) else Color.LightGray
                    )
                }
            }
        }
    }
}
