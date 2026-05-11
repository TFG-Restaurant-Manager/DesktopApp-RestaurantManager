package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Employee
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.CreateEmployeeState
import com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels.EmployeesViewModel
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.components.UiState
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import java.time.LocalDate

@Composable
fun EmployeesScreen(viewModel: EmployeesViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.employees.collectAsState()
    if (viewModel.loadRole() != "MANAGER") {
        ErrorScreen(
            title = Strings.t("screen.tables.error.title"),
            message = Strings.t("errors.permission")
        )
    } else {
        when (state) {
            is UiState.Error -> {
                ErrorScreen(
                    title = Strings.t("screen.employees.error.generic"),
                    message = (state as UiState.Error).message,
                    primaryAction = Pair(Strings.t("reload")) { viewModel.loadEmployees() }
                )
            }

            UiState.Idle -> {
                if (viewModel.loadRole() == "MANAGER") {
                    viewModel.loadEmployees()
                } else {
                    ErrorScreen(
                        title = Strings.t("screen.employees.error.generic"),
                        message = Strings.t("errors.permission"),
                    )
                }
            }

            UiState.Loading -> {
                LoadingScreen(
                    text = Strings.t("screen.employees.loading.text")
                )
            }

            is UiState.Success<List<Employee>> -> {
                val employees = (state as UiState.Success<List<Employee>>).data
                val createState by viewModel.createState.collectAsState()

                val editingEmployee = remember { mutableStateOf<Employee?>(null) }
                val deletingEmployee = remember { mutableStateOf<Employee?>(null) }
                val creatingEmployee = remember { mutableStateOf(false) }
                val editingPaswordEmployee = remember { mutableStateOf<Employee?>(null) }

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
                                text = Strings.t("screen.employees.title"),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = Strings.t("screen.employees.work"),
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
                            Text(
                                text = Strings.t("screen.employees.buttontext.addemployee"),
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
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
                                Text(
                                    text = Strings.t("screen.employees.text.totalemployees"),
                                    color = Color(0xFF64748B),
                                    fontSize = 14.sp
                                )
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
                                Text(
                                    text = Strings.t("screen.employees.text.actives"),
                                    color = Color(0xFF64748B),
                                    fontSize = 14.sp
                                )
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
                                Text(
                                    text = Strings.t("screen.employees.text.totalpayroll"),
                                    color = Color(0xFF64748B),
                                    fontSize = 14.sp
                                )
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
                                    Strings.t("screen.dishes.table.name"),
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF334155),
                                    modifier = Modifier.weight(1.5f)
                                )
                                Text(
                                    Strings.t("screen.employees.text.position"),
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF334155),
                                    modifier = Modifier.weight(1.5f)
                                )
                                Text(
                                    Strings.t("screen.employees.text.contact"),
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF334155),
                                    modifier = Modifier.weight(2f)
                                )
                                Text(
                                    Strings.t("screen.employees.text.status"),
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF334155),
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    Strings.t("screen.employees.text.actions"),
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF334155),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            HorizontalDivider(color = Color(0xFFE2E8F0))

                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(employees) { emp ->
                                    val isInactive = !emp.active

                                    Row(
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(horizontal = 24.dp, vertical = 16.dp),
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
                                                Text(emp.phone ?: "---", color = Color(0xFF64748B), fontSize = 14.sp)
                                            }
                                        }

                                        // Badge
                                        Box(modifier = Modifier.weight(1f)) {
                                            val badgeBg = if (isInactive) Color(0xFFF1F5F9) else Color(0xFFDCFCE7)
                                            val badgeText = if (isInactive) Color(0xFF64748B) else Color(0xFF16A34A)
                                            val badgeLabel = if (isInactive) Strings.t("screen.employees.text.inactive")
                                            else Strings.t("screen.employees.text.active")

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
                                            IconButton(
                                                onClick = { editingEmployee.value = emp },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Text("✎", color = Color(0xFF3B82F6), fontSize = 18.sp)
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            IconButton(
                                                onClick = { editingPaswordEmployee.value = emp },
                                                modifier = Modifier.size(36.dp)
                                            ) {
                                                Text("🔑", color = Color(0xFF3B82F6), fontSize = 18.sp)
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
                                    HorizontalDivider(color = Color(0xFFE2E8F0))
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

                    editingPaswordEmployee.value?.let { emp ->
                        EditEmployeePasswordDialog(
                            emp,
                            onDismiss = { editingPaswordEmployee.value = null },
                            onSave = { newPassword, employeeUpdated ->
                                viewModel.updateEmployeePassword(employeeUpdated, newPassword)
                                editingPaswordEmployee.value = null
                            }
                        )
                    }

                    deletingEmployee.value?.let { emp ->
                        AlertDialog(
                            onDismissRequest = { deletingEmployee.value = null },
                            title = { Text(text = Strings.t("screen.employees.text.confirmdeletion")) },
                            text = { Text(text = "¿${Strings.t("screen.employees.text.deletesomeone")} ${emp.name} (${emp.email})?") },
                            confirmButton = {
                                TextButton(onClick = {
                                    viewModel.deleteEmployee(emp)
                                    deletingEmployee.value = null
                                }) { Text(text = Strings.t("screen.employees.text.delete"), color = Color(0xFFEF4444)) }
                            },
                            dismissButton = {
                                TextButton(onClick = { deletingEmployee.value = null }) {
                                    Text(
                                        text = Strings.t("screen.tables.config.cancel"),
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditEmployeePasswordDialog(emp: Employee, onDismiss: () -> Unit, onSave: (String, Employee) -> Unit) {
    var password1 by remember { mutableStateOf("") }
    var password2 by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = Strings.t("screen.employees.text.changepassword"), fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                TextField(
                    value = password1,
                    onValueChange = { password1 = it },
                    label = { Text(text = Strings.t("login.password_label")) },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = password2,
                    onValueChange = { password2 = it },
                    label = { Text(text = Strings.t("screen.employees.text.repeatpassword")) },
                    modifier = Modifier.fillMaxWidth()
                )
                if (error.isNotEmpty()) {
                    Text(
                        error,
                        color = Color.Red
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (password1.isNotEmpty()) {
                    if (password1 == password2) {
                        onSave(password1, emp)
                    } else error = Strings.t("screen.employees.error.samepassword")
                } else error = Strings.t("screen.employees.error.emptypassword")
            }) { Text(text = Strings.t("screen.tables.config.save"), color = Color(0xFF3B82F6)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = Strings.t("screen.tables.config.cancel"),
                    color = Color(0xFF64748B)
                )
            }
        }
    )
}

@Composable
private fun EditEmployeeDialog(emp: Employee, onDismiss: () -> Unit, onSave: (Employee) -> Unit) {
    val roles = listOf("MANAGER", "WAITER", "COOKER")
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
        title = { Text(text = Strings.t("screen.employees.text.editemployee"), fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = Strings.t("screen.ingredient.form.name")) },
                    modifier = Modifier.fillMaxWidth()
                )
                val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}\$".toRegex()

                val isEmailError = email.isNotEmpty() && !email.matches(emailRegex)
                TextField(
                    value = email,
                    onValueChange = { email = it.trim() }, // trim() para evitar espacios accidentales
                    label = { Text(text = Strings.t("screen.employees.atribute.email")) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = isEmailError, // Se pone rojo si el formato es inválido
                    supportingText = {
                        if (isEmailError) {
                            Text(
                                text = "Formato de email inválido",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email, // Muestra el teclado con '@' y '.'
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
                TextField(
                    value = phone ?: "",
                    onValueChange = { newValue ->
                        // 1. Filtramos para que solo entren números
                        val onlyNumbers = newValue.filter { it.isDigit() }

                        // 2. Limitamos a 9 caracteres
                        if (onlyNumbers.length <= 9) {
                            phone = onlyNumbers
                        }
                    },
                    label = { Text(text = Strings.t("screen.employees.atribute.phone")) },
                    modifier = Modifier.fillMaxWidth(),
                    // Usamos Phone en lugar de Number para que en Android
                    // salgan símbolos como el "+" si fuera necesario en el futuro
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    isError = phone?.isNotEmpty() == true && (phone?.length ?: 0) < 9,
                    supportingText = {
                        if (phone?.isNotEmpty() == true && (phone?.length ?: 0) < 9) {
                            Text(
                                text = "Faltan dígitos (mínimo 9)",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                StableDateSelector(
                    Strings.t("screen.employees.atribute.startdate"),
                    { startDate = it },
                    emp.startDate
                )
                StableDateSelector(
                    Strings.t("screen.employees.atribute.enddate"),
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
                            text = Strings.t("screen.employees.atribute.active"),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = if (isActive) Strings.t("screen.employees.atribute.active.text.true") else Strings.t(
                                "screen.employees.atribute.active.text.false"
                            ),
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
                            text = if (isActive) Strings.t("screen.employees.atribute.active.value.true")
                            else Strings.t("screen.employees.atribute.active.value.false"),
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
                    label = { Text(text = Strings.t("screen.employees.atribute.positionnotes")) },
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
            }) { Text(text = Strings.t("screen.tables.config.save"), color = Color(0xFF3B82F6)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = Strings.t("screen.tables.config.cancel"),
                    color = Color(0xFF64748B)
                )
            }
        }
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

    // Estado para controlar si se ha intentado guardar (para mostrar errores)
    var showErrors by remember { mutableStateOf(false) }

    // Funciones de validación
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[a-z]{2,}\$".toRegex()
    val isNameInvalid = name.isBlank()
    val isEmailInvalid = email.isBlank() || !email.matches(emailRegex)
    val isPhoneInvalid = phone.isBlank() || phone.length < 9
    val isCodeInvalid = code.isBlank()
    val isPasswordInvalid = password.isBlank()
    val isStartDateInvalid = startDate.isBlank()
    val isEndDateInvalid = endDate.isBlank()

    val hasErrors = isNameInvalid || isEmailInvalid || isPhoneInvalid ||
            isCodeInvalid || isPasswordInvalid || isStartDateInvalid || isEndDateInvalid

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = { Text(text = Strings.t("screen.employees.buttontext.addemployee"), fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(text = Strings.t("screen.ingredient.form.name")) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = showErrors && isNameInvalid,
                    supportingText = {
                        if (showErrors && isNameInvalid) Text("El nombre es obligatorio")
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))

                val isEmailError = email.isNotEmpty() && !email.matches(emailRegex)
                TextField(
                    value = email,
                    onValueChange = { email = it.trim() }, // trim() para evitar espacios accidentales
                    label = { Text(text = Strings.t("screen.employees.atribute.email")) },
                    modifier = Modifier.fillMaxWidth(),
                    isError = showErrors && isEmailError, // Se pone rojo si el formato es inválido
                    supportingText = {
                        if (isEmailError) {
                            Text(
                                text = "Formato de email inválido",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email, // Muestra el teclado con '@' y '.'
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = phone,
                    onValueChange = { newValue ->
                        // 1. Filtramos para que solo entren números
                        val onlyNumbers = newValue.filter { it.isDigit() }

                        // 2. Limitamos a 9 caracteres
                        if (onlyNumbers.length <= 9) {
                            phone = onlyNumbers
                        }
                    },
                    label = { Text(text = Strings.t("screen.employees.atribute.phone")) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    // Usamos Phone en lugar de Number para que en Android
                    // salgan símbolos como el "+" si fuera necesario en el futuro
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    isError = showErrors && (phone.isNotEmpty() && phone.length < 9),
                    supportingText = {
                        if (phone.isNotEmpty() && phone.length < 9) {
                            Text(
                                text = "Faltan dígitos (mínimo 9)",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = code,
                    onValueChange = { if (it.length <= 10) code = it },
                    label = { Text(text = Strings.t("screen.employees.atribute.code")) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = showErrors && isCodeInvalid,
                    supportingText = { if (showErrors && isCodeInvalid) Text("El código es obligatorio") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = Strings.t("login.password_label")) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    isError = showErrors && isPasswordInvalid,
                    supportingText = { if (showErrors && isPasswordInvalid) Text("La contraseña es obligatoria") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column {
                    StableDateSelector(
                        Strings.t("screen.employees.atribute.startdate"),
                        { startDate = it }
                    )
                    if (showErrors && isStartDateInvalid) {
                        Text(
                            "Fecha de inicio obligatoria",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                Column {
                    StableDateSelector(
                        Strings.t("screen.employees.atribute.enddate"),
                        { endDate = it }
                    )
                    if (showErrors && isEndDateInvalid) {
                        Text(
                            "Fecha de fin obligatoria",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
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
                            text = Strings.t("screen.employees.atribute.active"),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF334155)
                        )
                        Text(
                            text = if (isActive) Strings.t("screen.employees.atribute.active.text.true") else Strings.t(
                                "screen.employees.atribute.active.text.false"
                            ),
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
                            text = if (isActive) Strings.t("screen.employees.atribute.active.value.true") else
                                Strings.t("screen.employees.atribute.active.value.false"),
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
                    label = { Text(text = Strings.t("screen.employees.atribute.positionnotes")) },
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
                    showErrors = true // Al pulsar, activamos la visualización de errores
                    if (!hasErrors) {
                        try {
                            val emp = Employee(
                                id = 0, roleName = role, name = name, email = email,
                                phone = phone, code = code, startDate = LocalDate.parse(startDate),
                                endDate = LocalDate.parse(endDate), active = isActive,
                                positionNotes = positionNotes, schedules = listOf()
                            )
                            onSave(emp, password)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
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
                    Text(text = Strings.t("generic.create"), color = Color(0xFFF97316))
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) {
                Text(text = Strings.t("screen.tables.config.cancel"), color = Color(0xFF64748B))
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
                onValueChange = { newValue ->
                    val onlyNumbers = newValue.filter { it.isDigit() }

                    if (onlyNumbers.length <= 2) {
                        if (onlyNumbers.isEmpty()) {
                            day = ""
                        } else {
                            val dayInt = onlyNumbers.toInt()
                            val maxDays = try {
                                val monthInt = month.toIntOrNull() ?: 1
                                java.time.YearMonth.of(2024, monthInt).lengthOfMonth()
                            } catch (e: Exception) {
                                31
                            }

                            if (dayInt <= maxDays) {
                                day = onlyNumbers
                            }
                        }
                    }
                },
                modifier = Modifier.weight(0.3f),
                label = { Text(Strings.t("screen.employees.text.days.short")) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Abre teclado numérico
            )
            OutlinedTextField(
                value = month,
                onValueChange = { newValue ->
                    val onlyNumbers = newValue.filter { it.isDigit() }

                    if (onlyNumbers.length <= 2) {
                        if (onlyNumbers.isEmpty()) {
                            month = ""
                        } else {
                            val monthInt = onlyNumbers.toInt()
                            // Validamos que no sea mayor a 12
                            if (monthInt <= 12) {
                                // Opcional: Evitar que sea "00"
                                month = onlyNumbers
                            } else {
                                month = "12" // Si pone 13, 14... forzamos a 12
                            }
                        }
                    }
                },
                modifier = Modifier.weight(0.3f),
                label = { Text(Strings.t("screen.employees.text.moths.short")) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            OutlinedTextField(
                value = year,
                onValueChange = { newValue ->
                    val onlyNumbers = newValue.filter { it.isDigit() }

                    if (onlyNumbers.length <= 4) {
                        year = onlyNumbers
                    }
                },
                modifier = Modifier.weight(0.4f),
                label = { Text(Strings.t("screen.employees.text.years.short")) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }

        // Al cambiar cualquier valor, notificamos la fecha completa
        LaunchedEffect(day, month, year) {
            if (day.length == 2 && month.length == 2 && year.length == 4) {
                onDateSelected("$year-$month-$day")
            }
        }
    }
}
