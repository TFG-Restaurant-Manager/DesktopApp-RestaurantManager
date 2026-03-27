package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.LoginService
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    val service: LoginService
) : ViewModel() {
    private val _title = MutableStateFlow(Strings.t("screen.login.title"))
    val title: StateFlow<String> = _title.asStateFlow()

    fun loadLogin() {
        viewModelScope.launch {
            service.loadInitialData()
        }
    }

    fun clear() {

    }
}
