package com.tfg_rm.desktopapp_restaurantmanager.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tfg_rm.desktopapp_restaurantmanager.ui.navigation.AppScreens
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings

@Composable
fun LoginScreen(
    navigate: (String) -> Unit
) {
    val employeeCode = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF6F2), Color(0xFFFFF0EC))
                )
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.44f)
                .shadow(12.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(28.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    color = Color(0xFFFF7A00)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(text = "🍴", fontSize = 24.sp)
                    }
                }

                Text(
                    text = Strings.t("app.name"),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
                )
                Text(text = Strings.t("app.subtitle"), color = Color.Gray)

                Column(modifier = Modifier.padding(top = 18.dp)) {
                    Text(text = Strings.t("login.employee_code_label"), fontWeight = FontWeight.Medium)
                    OutlinedTextField(
                        value = employeeCode.value,
                        onValueChange = { employeeCode.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        singleLine = true,
                        placeholder = { Text(Strings.t("login.employee_code_placeholder")) },
                        shape = RoundedCornerShape(8.dp)
                    )

                    Text(text = Strings.t("login.password_label"), fontWeight = FontWeight.Medium, modifier = Modifier.padding(top = 12.dp))
                    OutlinedTextField(
                        value = password.value,
                        onValueChange = { password.value = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        singleLine = true,
                        placeholder = { Text(Strings.t("login.password_placeholder")) },
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Button(
                        onClick = { navigate(AppScreens.MainScreen.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7A00), contentColor = Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Text(text = Strings.t("login.button"))
                        }
                    }
                }
            }
        }
    }
}
