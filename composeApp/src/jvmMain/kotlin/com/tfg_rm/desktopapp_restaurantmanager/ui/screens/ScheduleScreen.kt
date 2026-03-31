package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.ScheduleViewModel
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

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
fun ScheduleScreen(viewModel: ScheduleViewModel, modifier: Modifier = Modifier) {
    val employees by viewModel.employees.collectAsState()
    val shifts    by viewModel.shifts.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadSchedule() }

    // Editing state: pair of (employee, day) being edited
    var editTarget by remember { mutableStateOf<Pair<Employee, DayOfWeek>?>(null) }

    // Current week range
    val today      = LocalDate.now()
    val weekStart  = today.with(DayOfWeek.MONDAY)
    val weekEnd    = today.with(DayOfWeek.SUNDAY)
    val fmt        = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("es"))
    val totalHours = shifts.sumOf {
        val mins = java.time.Duration.between(it.startDateTime, it.endDateTime).toMinutes()
        if (mins < 0) 0L else mins
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
                        drawLine(color = Color(0xFFF97316), start = center, end = Offset(center.x, center.y - 6.dp.toPx()), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                        drawLine(color = Color(0xFFF97316), start = center, end = Offset(center.x + 4.dp.toPx(), center.y), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
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
                        text = "Haz clic en cualquier celda", 
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF1D4ED8),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " para editar el horario del empleado. Usa los turnos predefinidos o crea horarios personalizados.", 
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF1D4ED8),
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Weekly table
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize().horizontalScroll(rememberScrollState())) {
                Column(modifier = Modifier.width(1208.dp)) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
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
                    
                    Divider(color = Color(0xFFE2E8F0))
                    
                    // Employee Rows
                    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                        employees.forEach { emp ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Employee info
                                Column(modifier = Modifier.width(180.dp).padding(end = 16.dp)) {
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
                                
                                // Day columns
                                DAYS.forEach { day ->
                                    val shift = shifts.find { it.employeeRestaurantId == emp.id && it.startDateTime.dayOfWeek == day }
                                    Box(modifier = Modifier.width(140.dp).padding(horizontal = 8.dp)) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(42.dp)
                                                .background(
                                                    color = if (shift != null) Color(0xFFDCFCE7) else Color(0xFFF1F5F9), 
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .clickable { editTarget = Pair(emp, day) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (shift != null) {
                                                Text(
                                                    text = "${shift.startDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))}-${shift.endDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF16A34A)
                                                )
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
                            Divider(color = Color(0xFFE2E8F0))
                        }
                    }
                }
            }
        }
    }

    // Shift edit dialog
    editTarget?.let { (emp, day) ->
        ShiftEditDialog(
            employee = emp,
            day = day,
            currentShift = shifts.find { it.employeeRestaurantId == emp.id && it.startDateTime.dayOfWeek == day },
            onDismiss = { editTarget = null },
            onSave = { shift ->
                viewModel.setShift(shift)
                editTarget = null
            },
            onRemove = {
                viewModel.removeShift(emp.id, day)
                editTarget = null
            }
        )
    }
}

@Composable
private fun ShiftEditDialog(
    employee: Employee,
    day: DayOfWeek,
    currentShift: Shift?,
    onDismiss: () -> Unit,
    onSave: (Shift) -> Unit,
    onRemove: () -> Unit
) {
    var startText by remember { mutableStateOf(currentShift?.startDateTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "09:00") }
    var endText   by remember { mutableStateOf(currentShift?.endDateTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "17:00") }
    var error     by remember { mutableStateOf<String?>(null) }

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
                                        endText   = parts[1]
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) { 
                                Text(text = preset, fontSize = 13.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium) 
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
                                        endText   = parts[1]
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) { 
                                Text(text = preset, fontSize = 13.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium) 
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                Divider(color = Color(0xFFE2E8F0))
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
                    Text(text = error!!, color = Color(0xFFEF4444), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val fmt2 = DateTimeFormatter.ofPattern("HH:mm")
                        val shiftDate = LocalDate.now().with(DayOfWeek.MONDAY).plusDays((day.value - 1).toLong())
                        val startTime = LocalTime.parse(startText.trim(), fmt2)
                        val endTime   = LocalTime.parse(endText.trim(), fmt2)
                        if (!endTime.isAfter(startTime)) { error = Strings.t("screen.shift.error.end_after_start") ; return@Button }
                        val start = LocalDateTime.of(shiftDate, startTime)
                        val end   = LocalDateTime.of(shiftDate, endTime)
                        onSave(Shift(employeeRestaurantId = employee.id, startDateTime = start, endDateTime = end))
                    } catch (e: Exception) {
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
                if (currentShift != null) {
                    OutlinedButton(
                        onClick = onRemove,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        border = BorderStroke(1.dp, Color(0xFFEF4444)),
                        shape = RoundedCornerShape(8.dp)
                    ) { 
                        Text(text = Strings.t("screen.shift.remove"), fontWeight = FontWeight.SemiBold) 
                    }
                }
                TextButton(onClick = onDismiss) { 
                    Text(text = Strings.t("screen.shift.cancel"), color = Color(0xFF64748B), fontWeight = FontWeight.SemiBold) 
                }
            }
        }
    )
}
