package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.CreateEmployeeState
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.EmployeesViewModel
import java.time.LocalDate

@Composable
fun EmployeesScreen(viewModel: EmployeesViewModel, modifier: Modifier = Modifier) {
    val employees by viewModel.employees.collectAsState()
    val createState by viewModel.createState.collectAsState()

    val editingEmployee = remember { mutableStateOf<Employee?>(null) }
    val deletingEmployee = remember { mutableStateOf<Employee?>(null) }
    val creatingEmployee = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadEmployees() }
    LaunchedEffect(createState) {
        if (createState is CreateEmployeeState.Success) {
            creatingEmployee.value = false
            viewModel.resetCreateState()
        }
    }

    // Fallback static metrics
    val activeEmployees = employees.count { it.active }
    val totalPayroll = employees.size * 1500

    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Empleados",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Gestiona el personal del restaurante",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF64748B)
                )
            }
            Button(
                onClick = { creatingEmployee.value = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF97316)),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(text = "+", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Añadir Empleado", color = Color.White, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Top metrics (cards)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 24.dp, horizontal = 24.dp)) {
                    Text(text = "Total Empleados", color = Color(0xFF64748B), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = employees.size.toString(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 24.dp, horizontal = 24.dp)) {
                    Text(text = "Activos", color = Color(0xFF64748B), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = activeEmployees.toString(),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF22C55E)
                    )
                }
            }
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 24.dp, horizontal = 24.dp)) {
                    Text(text = "Nómina Total", color = Color(0xFF64748B), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${totalPayroll}€",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Table-like list
        Card(
            modifier = Modifier.fillMaxWidth().weight(1f),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Table header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Nombre",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155),
                        modifier = Modifier.weight(1.5f)
                    )
                    Text(
                        "Puesto",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155),
                        modifier = Modifier.weight(1.5f)
                    )
                    Text(
                        "Contacto",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155),
                        modifier = Modifier.weight(2f)
                    )
                    Text(
                        "Salario",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "Estado",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155),
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "Acciones",
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF334155),
                        modifier = Modifier.weight(1f)
                    )
                }

                Divider(color = Color(0xFFE2E8F0))

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(employees) { emp ->
                        val isInactive = !emp.active

                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                emp.name,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0F172A),
                                modifier = Modifier.weight(1.5f)
                            )
                            Text(emp.roleName, color = Color(0xFF64748B), modifier = Modifier.weight(1.5f))

                            Column(modifier = Modifier.weight(2f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("✉", color = Color(0xFF94A3B8), fontSize = 14.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(emp.email, color = Color(0xFF64748B), fontSize = 14.sp)
                                }
                                Spacer(Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("☎", color = Color(0xFF94A3B8), fontSize = 14.sp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(emp.phone!!, color = Color(0xFF64748B), fontSize = 14.sp)
                                }
                            }

                            // Mocking salary based on standard assumption
                            val mockSalary = if (emp.roleName.contains(
                                    "Gerente",
                                    ignoreCase = true
                                )
                            ) "2800€/mes" else if (emp.roleName.contains(
                                    "Chef",
                                    ignoreCase = true
                                )
                            ) "2500€/mes" else "1400€/mes"
                            Text(
                                mockSalary,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF0F172A),
                                modifier = Modifier.weight(1f)
                            )

                            // Badge
                            Box(modifier = Modifier.weight(1f)) {
                                val badgeBg = if (isInactive) Color(0xFFF1F5F9) else Color(0xFFDCFCE7)
                                val badgeText = if (isInactive) Color(0xFF64748B) else Color(0xFF16A34A)
                                val badgeLabel = if (isInactive) "Inactivo" else "Activo"

                                Surface(
                                    color = badgeBg,
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = badgeLabel,
                                        color = badgeText,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Row(modifier = Modifier.weight(1f)) {
                                IconButton(onClick = { editingEmployee.value = emp }, modifier = Modifier.size(36.dp)) {
                                    Text("✎", color = Color(0xFF3B82F6), fontSize = 18.sp)
                                }
                                Spacer(Modifier.width(8.dp))
                                IconButton(
                                    onClick = { deletingEmployee.value = emp },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Text("🗑", color = Color(0xFFEF4444), fontSize = 18.sp)
                                }
                            }
                        }
                        Divider(color = Color(0xFFE2E8F0))
                    }
                }
            }
        }

        // Dialogs
        if (creatingEmployee.value) {
            NewEmployeeDialog(
                createState = createState,
                onDismiss = {
                    creatingEmployee.value = false
                    viewModel.resetCreateState()
                },
                onSave = { newEmp, password ->
                    viewModel.addEmployee(newEmp, password)
                }
            )
        }

        editingEmployee.value?.let { emp ->
            EditEmployeeDialog(
                emp = emp,
                onDismiss = { editingEmployee.value = null },
                onSave = { updated ->
                    viewModel.updateEmployee(updated)
                    editingEmployee.value = null
                }
            )
        }

        deletingEmployee.value?.let { emp ->
            AlertDialog(
                onDismissRequest = { deletingEmployee.value = null },
                title = { Text(text = "Confirmar borrado") },
                text = { Text(text = "¿Eliminar a ${emp.name} (${emp.email})?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deleteEmployee(emp)
                        deletingEmployee.value = null
                    }) { Text(text = "Eliminar", color = Color(0xFFEF4444)) }
                },
                dismissButton = {
                    TextButton(onClick = { deletingEmployee.value = null }) {
                        Text(
                            text = "Cancelar",
                            color = Color(0xFF64748B)
                        )
                    }
                }
            )
        }
    }
}

@Composable
private fun EditEmployeeDialog(emp: Employee, onDismiss: () -> Unit, onSave: (Employee) -> Unit) {
    val roles = listOf("MANAGER", "WAITER", "COOKER", "ADMIN")
    var roleExpanded by remember { mutableStateOf(false) }
    var role by remember { mutableStateOf(emp.roleName) }
    var name by remember { mutableStateOf(emp.name) }
    var phone by remember { mutableStateOf(emp.phone) }
    var email by remember { mutableStateOf(emp.email) }
    var isActive by remember { mutableStateOf(emp.active) }
    var startDate by remember { mutableStateOf(emp.startDate.toString()) }
    var endDate by remember { mutableStateOf(emp.endDate.toString()) }
    var positionNotes by remember { mutableStateOf(emp.positionNotes) }

    LaunchedEffect(emp) {
        role = emp.roleName
        name = emp.name
        phone = emp.phone
        email = emp.email
        isActive = emp.active
        startDate = emp.startDate.toString()
        endDate = emp.endDate.toString()
        positionNotes = emp.positionNotes
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar empleado", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = "Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = "Teléfono") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = phone!!,
                    onValueChange = { phone = it },
                    label = { Text(text = "Teléfono") },
                    modifier = Modifier.fillMaxWidth()
                )
                StableDateSelector(
                    "Fecha de inicio (AAAA-MM-DD)",
                    { startDate = it },
                    emp.startDate
                )
                StableDateSelector(
                    "Fecha de fin (AAAA-MM-DD)",
                    { endDate = it },
                    emp.endDate
                )
                // Active switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Estado del empleado",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = if (isActive) "El empleado puede acceder al sistema" else "Acceso restringido",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B)
                        )
                    }

                    Button(
                        onClick = { isActive = !isActive },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isActive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                            contentColor = if (isActive) Color(0xFF16A34A) else Color(0xFFEF4444)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isActive) Color(0xFF22C55E).copy(alpha = 0.5f) else Color(0xFFEF4444).copy(alpha = 0.5f)
                        ),
                        enabled = true
                    ) {
                        Text(
                            text = if (isActive) "ACTIVO" else "INACTIVO",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
                // Role dropdown
                Box {
                    OutlinedButton(
                        onClick = { roleExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Text(text = role, color = Color(0xFF334155))
                    }
                    DropdownMenu(
                        expanded = roleExpanded,
                        onDismissRequest = { roleExpanded = false }
                    ) {
                        roles.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(text = r) },
                                onClick = { role = r; roleExpanded = false }
                            )
                        }
                    }
                }
                TextField(
                    value = positionNotes ?: "",
                    onValueChange = { positionNotes = it },
                    label = { Text(text = "Notas posición") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(
                    emp.copy(
                        roleName = role,
                        name = name,
                        phone = phone,
                        positionNotes = positionNotes,
                        email = email,
                        endDate = LocalDate.parse(endDate),
                        startDate = LocalDate.parse(startDate),
                        active = isActive
                    )
                )
            }) { Text(text = "Guardar", color = Color(0xFF3B82F6)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(text = "Cancelar", color = Color(0xFF64748B)) } }
    )
}

@Composable
private fun NewEmployeeDialog(
    createState: CreateEmployeeState,
    onDismiss: () -> Unit,
    onSave: (Employee, String) -> Unit
) {
    val roles = listOf("MANAGER", "WAITER", "COOKER", "ADMIN")
    var role by remember { mutableStateOf(roles[0]) }
    var roleExpanded by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var isActive by remember { mutableStateOf(true) }
    var positionNotes by remember { mutableStateOf("") }

    val isLoading = createState is CreateEmployeeState.Loading
    val errorMsg = (createState as? CreateEmployeeState.Error)?.msg

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text(text = "Añadir empleado", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = "Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = "Email") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(text = "Teléfono (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = code,
                    onValueChange = { if (it.length <= 10) code = it },
                    label = { Text(text = "Código empleado") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = "Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                Spacer(modifier = Modifier.height(12.dp))
                StableDateSelector(
                    "Fecha de inicio (AAAA-MM-DD)",
                    { startDate = it }
                )
                Spacer(modifier = Modifier.height(12.dp))
                StableDateSelector(
                    "Fecha de fin (AAAA-MM-DD)",
                    { endDate = it }
                )
                Spacer(modifier = Modifier.height(12.dp))
                // Active switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Estado del empleado",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = if (isActive) "El empleado puede acceder al sistema" else "Acceso restringido",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B)
                        )
                    }

                    Button(
                        onClick = { if (!isLoading) isActive = !isActive },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isActive) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                            contentColor = if (isActive) Color(0xFF16A34A) else Color(0xFFEF4444)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isActive) Color(0xFF22C55E).copy(alpha = 0.5f) else Color(0xFFEF4444).copy(alpha = 0.5f)
                        ),
                        enabled = !isLoading
                    ) {
                        Text(
                            text = if (isActive) "ACTIVO" else "INACTIVO",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                // Role dropdown
                Box {
                    OutlinedButton(
                        onClick = { if (!isLoading) roleExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Text(text = role, color = Color(0xFF334155))
                    }
                    DropdownMenu(
                        expanded = roleExpanded,
                        onDismissRequest = { roleExpanded = false }
                    ) {
                        roles.forEach { r ->
                            DropdownMenuItem(
                                text = { Text(text = r) },
                                onClick = { role = r; roleExpanded = false }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = positionNotes,
                    onValueChange = { if (it.length <= 10) positionNotes = it },
                    label = { Text(text = "Notas de la posicion") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMsg,
                        color = Color(0xFFEF4444),
                        fontSize = 13.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    try {
                        val emp = Employee(
                            id = 0, roleName = role, name = name, email = email,
                            phone = phone, code = code, startDate = LocalDate.parse(startDate),
                            endDate = LocalDate.parse(endDate), active = isActive, positionNotes = positionNotes,
                            schedules = listOf()
                        )
                        onSave(emp, password)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        println("Error: ${e.message}")
                    }
                },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp,
                        color = Color(0xFFF97316)
                    )
                } else {
                    Text(text = "Crear", color = Color(0xFFF97316))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text(text = "Cancelar", color = Color(0xFF64748B))
            }
        }
    )
}

@Composable
fun StableDateSelector(
    label: String,
    onDateSelected: (String) -> Unit,
    value: LocalDate? = null
) {
    var day by remember { mutableStateOf("01") }
    var month by remember { mutableStateOf("01") }
    var year by remember { mutableStateOf("2024") }

    LaunchedEffect(value) {
        value?.let {
            day = it.dayOfMonth.toString().padStart(2, '0')
            month = it.monthValue.toString().padStart(2, '0')
            year = it.year.toString()
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Campos de texto pequeños para cada parte
            OutlinedTextField(
                value = day,
                onValueChange = { if (it.length <= 2) day = it },
                modifier = Modifier.weight(0.3f),
                label = { Text("DD") })
            OutlinedTextField(
                value = month,
                onValueChange = { if (it.length <= 2) month = it },
                modifier = Modifier.weight(0.3f),
                label = { Text("MM") })
            OutlinedTextField(
                value = year,
                onValueChange = { if (it.length <= 4) year = it },
                modifier = Modifier.weight(0.4f),
                label = { Text("AAAA") })
        }

        // Al cambiar cualquier valor, notificamos la fecha completa
        LaunchedEffect(day, month, year) {
            if (day.length == 2 && month.length == 2 && year.length == 4) {
                onDateSelected("$year-$month-$day")
            }
        }
    }
}
