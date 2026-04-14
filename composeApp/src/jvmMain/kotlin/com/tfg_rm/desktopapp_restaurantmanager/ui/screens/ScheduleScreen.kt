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
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.EmployeesViewModel
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.SaveScheduleState
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
                primaryAction = Pair(Strings.t("reload"), { viewModel.loadEmployees() })
            )
        }

        UiState.Loading -> {
            LoadingScreen(Strings.t("screen.shift.loading.message"))
        }

        is UiState.Success<List<Employee>> -> {
            var weekStart by remember { mutableStateOf(LocalDate.now().with(DayOfWeek.MONDAY)) }
            val employees = (state as UiState.Success<List<Employee>>).data
            val saveState = viewModel.scheduleState
            // Editing state: pair of (employee, day) being edited
            var editTarget by remember { mutableStateOf<Pair<Employee, DayOfWeek>?>(null) }

            val weekEnd = weekStart.plusDays(6)
            val fmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("es"))


            val totalHours = employees.sumOf { employee ->
                employee.schedules.sumOf {
                    val mins = java.time.Duration.between(it.first, it.second).toMinutes()
                    if (mins < 0) 0L else mins
                }
            } / 60

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
                                    onClick = { weekStart.minusDays(7) },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF475569)),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text(text = "←", fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(
                                    onClick = { weekStart.plusDays(7) },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF475569)),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Text(text = "→", fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { viewModel.saveSchedules() },
                                    enabled = saveState !is SaveScheduleState.Loading,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    if (saveState is SaveScheduleState.Loading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(
                                            text = if (saveState is SaveScheduleState.Success) "✓ Guardado" else "Guardar",
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                            if (saveState is SaveScheduleState.Error) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = (saveState as SaveScheduleState.Error).message,
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
                    { editTarget = it }
                )
            }

            // Shift edit dialog
            editTarget?.let { (emp, day) ->
                val shiftDate = weekStart.plusDays((day.value - 1).toLong())
                ShiftEditDialog(
                    employee = emp,
                    day = day,
                    weekStart = weekStart,
                    currentShift = emp.schedules.filter { it.first.toLocalDate().isEqual(shiftDate) },
                    onDismiss = { editTarget = null },
                    onSave = { employee ->
                        viewModel.updateEmployee(employee)
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

                                val shift = emp.schedules

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
                                                        text = "${pair.first.format(DateTimeFormatter.ofPattern("HH:mm"))}-${
                                                            pair.second.format(
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

                        // 🔥 DIVIDER FULL WIDTH (IMPORTANTE)
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
    currentShift: List<Pair<LocalDateTime, LocalDateTime>>,
    onDismiss: () -> Unit,
    onSave: (Employee) -> Unit
) {
    var startText by remember {
        mutableStateOf(
            currentShift.firstOrNull()?.first?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "09:00"
        )
    }
    var endText by remember {
        mutableStateOf(
            currentShift.firstOrNull()?.second?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "17:00"
        )
    }
    var error by remember { mutableStateOf<String?>(null) }

    // Preset shifts
    val presets = listOf("09:00-17:00", "10:00-18:00", "12:00-20:00", "11:00-19:00", "08:00-16:00")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = String.format(Strings.t("screen.shift.title_format"), employee.name, dayNames[day]),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF0F172A)
            )
        },
        text = {
            Column(modifier = Modifier.widthIn(min = 320.dp)) {
                Text(
                    text = Strings.t("screen.shift.presets_label"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        presets.take(3).forEach { preset ->
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                    .clickable {
                                        val parts = preset.split("-")
                                        startText = parts[0]
                                        endText = parts[1]
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = preset,
                                    fontSize = 13.sp,
                                    color = Color(0xFF475569),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        presets.drop(3).forEach { preset ->
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                                    .clickable {
                                        val parts = preset.split("-")
                                        startText = parts[0]
                                        endText = parts[1]
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = preset,
                                    fontSize = 13.sp,
                                    color = Color(0xFF475569),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color(0xFFE2E8F0))
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = Strings.t("screen.shift.custom_title"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = startText,
                        onValueChange = { startText = it; error = null },
                        label = { Text(Strings.t("screen.shift.start_label"), color = Color(0xFF64748B)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Text(text = "→", color = Color(0xFF94A3B8), fontSize = 20.sp)
                    OutlinedTextField(
                        value = endText,
                        onValueChange = { endText = it; error = null },
                        label = { Text(Strings.t("screen.shift.end_label"), color = Color(0xFF64748B)) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                }
                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
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
                        val fmt2 = DateTimeFormatter.ofPattern("HH:mm")
                        val shiftDate = weekStart.plusDays((day.value - 1).toLong())
                        val startTime = LocalTime.parse(startText.trim(), fmt2)
                        val endTime = LocalTime.parse(endText.trim(), fmt2)
                        if (!endTime.isAfter(startTime)) {
                            error = Strings.t("screen.shift.error.end_after_start"); return@Button
                        }
                        onSave(employee)
                    } catch (_: Exception) {
                        error = Strings.t("screen.shift.error.format_invalid")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = Strings.t("screen.shift.save"), color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (!currentShift.isEmpty()) {
                    OutlinedButton(
                        onClick = { onSave(employee) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = Strings.t("screen.shift.remove"), fontWeight = FontWeight.SemiBold)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(
                        text = Strings.t("screen.shift.cancel"),
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    )
}
