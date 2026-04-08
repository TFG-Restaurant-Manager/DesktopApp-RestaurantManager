package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.CreateEmployeeState
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.EmployeesViewModel
import kotlin.collections.listOf

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
                    Text(text = employees.size.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
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
                    Text(text = activeEmployees.toString(), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF22C55E))
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
                    Text(text = "${totalPayroll}€", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
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
                    Text("Nombre", fontWeight = FontWeight.SemiBold, color = Color(0xFF334155), modifier = Modifier.weight(1.5f))
                    Text("Puesto", fontWeight = FontWeight.SemiBold, color = Color(0xFF334155), modifier = Modifier.weight(1.5f))
                    Text("Contacto", fontWeight = FontWeight.SemiBold, color = Color(0xFF334155), modifier = Modifier.weight(2f))
                    Text("Salario", fontWeight = FontWeight.SemiBold, color = Color(0xFF334155), modifier = Modifier.weight(1f))
                    Text("Estado", fontWeight = FontWeight.SemiBold, color = Color(0xFF334155), modifier = Modifier.weight(1f))
                    Text("Acciones", fontWeight = FontWeight.SemiBold, color = Color(0xFF334155), modifier = Modifier.weight(1f))
                }
                
                Divider(color = Color(0xFFE2E8F0))

                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(employees) { emp ->
                        val isInactive = emp.name.contains("Inactivo", ignoreCase = true) || employees.indexOf(emp) == employees.size - 1
                        
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(emp.name, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A), modifier = Modifier.weight(1.5f))
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
                            val mockSalary = if (emp.roleName.contains("Gerente", ignoreCase = true)) "2800€/mes" else if (emp.roleName.contains("Chef", ignoreCase = true)) "2500€/mes" else "1400€/mes"
                            Text(mockSalary, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A), modifier = Modifier.weight(1f))
                            
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
                                IconButton(onClick = { deletingEmployee.value = emp }, modifier = Modifier.size(36.dp)) {
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
                        viewModel.deleteEmployee(emp.email)
                        deletingEmployee.value = null
                    }) { Text(text = "Eliminar", color = Color(0xFFEF4444)) }
                },
                dismissButton = { TextButton(onClick = { deletingEmployee.value = null }) { Text(text = "Cancelar", color = Color(0xFF64748B)) } }
            )
        }
    }
}

@Composable
private fun EditEmployeeDialog(emp: Employee, onDismiss: () -> Unit, onSave: (Employee) -> Unit) {
    var role by remember { mutableStateOf(emp.roleName) }
    var name by remember { mutableStateOf(emp.name) }
    var phone by remember { mutableStateOf(emp.phone) }

    LaunchedEffect(emp) {
        role = emp.roleName
        name = emp.name
        phone = emp.phone
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar empleado", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                TextField(value = role, onValueChange = { role = it }, label = { Text(text = "Rol") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                TextField(value = name, onValueChange = { name = it }, label = { Text(text = "Nombre") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Email: ${emp.email}", color = Color.Gray, modifier = Modifier.padding(start = 4.dp))
                Spacer(modifier = Modifier.height(12.dp))
                TextField(value = phone!!, onValueChange = { phone = it }, label = { Text(text = "Teléfono") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(emp.copy(roleName = role, name = name, phone = phone)) }) { Text(text = "Guardar", color = Color(0xFF3B82F6)) }
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
                    onValueChange = { if (it.length <= 10) code = it.filter { c -> c.isDigit() } },
                    label = { Text(text = "Código empleado (10 dígitos)") },
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
                TextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text(text = "Fecha de inicio (AAAA-MM-DD)") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
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
                    val emp = Employee(
                        id = 0, roleName = role, name = name, email = email,
                        phone = phone, code = code, startDate = startDate, active = true,
                        schedules = listOf()
                    )
                    onSave(emp, password)
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
