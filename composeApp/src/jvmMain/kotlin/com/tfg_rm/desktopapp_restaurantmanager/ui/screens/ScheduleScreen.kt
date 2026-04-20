package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
                    weekStart = weekStart
                ) { editTarget = it }
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

@Composable
private fun WeeklyTable(
    employees: List<Employee>,
    weekStart: LocalDate,
    editTarget: (Pair<Employee, DayOfWeek>) -> Unit
) {
    val horizontalScroll = rememberScrollState()
    val verticalScroll = rememberScrollState()

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(horizontalScroll)
        ) {

            Column(
                modifier = Modifier.width(IntrinsicSize.Max)
            ) {

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
                        modifier = Modifier.width(180.dp)
                    )

                    DAYS.forEach { day ->
                        Text(
                            text = dayNames[day] ?: "",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155),
                            modifier = Modifier.width(140.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                // BODY (VERTICAL SCROLL)
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
                                    .width(180.dp)
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
                            }

                            // DAYS
                            DAYS.forEach { day ->

                                val shift = emp.schedules.filter {
                                    it.startDateTime.toLocalDate().isEqual(weekStart.plusDays((day.value - 1).toLong()))
                                }

                                Box(
                                    modifier = Modifier
                                        .width(140.dp)
                                        .padding(horizontal = 8.dp)
                                ) {

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(42.dp)
                                            .background(
                                                color = if (shift.isNotEmpty())
                                                    Color(0xFFDCFCE7)
                                                else
                                                    Color(0xFFF1F5F9),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable {
                                                editTarget(Pair(emp, day))
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {

                                        if (shift.isNotEmpty()) {
                                            Column {
                                                shift.forEach { pair ->
                                                    Text(
                                                        text = "${
                                                            pair.startDateTime.format(
                                                                DateTimeFormatter.ofPattern(
                                                                    "HH:mm"
                                                                )
                                                            )
                                                        }-${
                                                            pair.endDateTime.format(
                                                                DateTimeFormatter.ofPattern("HH:mm")
                                                            )
                                                        }",
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color(0xFF16A34A)
                                                    )
                                                }
                                            }
                                        } else {
                                            Text(
                                                text = Strings.t("screen.schedule.day.rest"),
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Medium,
                                                color = Color(0xFF94A3B8)
                                            )
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
                        startDateTime = LocalDateTime.of(
                            shiftDate,
                            LocalTime.of(9, 0)
                        ),
                        endDateTime = LocalDateTime.of(
                            shiftDate,
                            LocalTime.of(17, 0)
                        )
                    )
                )
            }
        )
    }

    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = String.format(
                    Strings.t("screen.shift.title_format"),
                    employee.name,
                    dayNames[day]
                ) + " ${shiftDate.dayOfMonth} ${shiftDate.month.name} ${shiftDate.year}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF0F172A)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .widthIn(min = 420.dp)
                    .verticalScroll(rememberScrollState())
            ) {

                schedules.forEachIndexed { index, shift ->

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        OutlinedTextField(
                            value = shift.startDateTime.format(formatter),
                            onValueChange = { newStart ->
                                try {
                                    val startTime = LocalTime.parse(newStart, formatter)

                                    schedules = schedules.toMutableList().apply {
                                        this[index] = shift.copy(
                                            startDateTime = LocalDateTime.of(
                                                shift.startDateTime.toLocalDate(),
                                                startTime
                                            )
                                        )
                                    }

                                    error = null
                                } catch (_: Exception) {
                                }
                            },
                            label = {
                                Text(
                                    Strings.t("screen.shift.start_label"),
                                    color = Color(0xFF64748B)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        OutlinedTextField(
                            value = shift.endDateTime.format(formatter),
                            onValueChange = { newEnd ->
                                try {
                                    val endTime = LocalTime.parse(newEnd, formatter)

                                    schedules = schedules.toMutableList().apply {
                                        this[index] = shift.copy(
                                            endDateTime = LocalDateTime.of(
                                                shift.endDateTime.toLocalDate(),
                                                endTime
                                            )
                                        )
                                    }

                                    error = null
                                } catch (_: Exception) {
                                }
                            },
                            label = {
                                Text(
                                    Strings.t("screen.shift.end_label"),
                                    color = Color(0xFF64748B)
                                )
                            },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                schedules = schedules.toMutableList().apply {
                                    removeAt(index)
                                }
                            }
                        ) {
                            Text(
                                text = "✕",
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (schedules.size < 4) {
                    OutlinedButton(
                        onClick = {
                            schedules = schedules + Shift(
                                startDateTime = LocalDateTime.of(
                                    shiftDate,
                                    LocalTime.of(9, 0)
                                ),
                                endDateTime = LocalDateTime.of(
                                    shiftDate,
                                    LocalTime.of(17, 0)
                                )
                            )
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("+ ${Strings.t("screen.shift.add_shift")}")
                    }
                }

                if (error != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = error!!,
                        color = Color(0xFFEF4444),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        schedules.forEach { shift ->
                            if (!shift.endDateTime.toLocalTime()
                                    .isAfter(shift.startDateTime.toLocalTime())
                            ) {
                                throw IllegalArgumentException(
                                    Strings.t("screen.shift.error.end_after_start")
                                )
                            }
                        }

                        onSave(employee, schedules)
                    } catch (e: Exception) {
                        error = e.message ?: Strings.t("screen.shift.error.format_invalid")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = Strings.t("screen.shift.save"),
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = Strings.t("screen.shift.cancel"),
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    )
}
