package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.SessionManager
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.LoginService
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class LogOut(val error: Boolean = false) : AuthState()
    data class Error(val msg: String) : AuthState()
}

class LoginViewModel(
    private val service: LoginService
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        viewModelScope.launch {
            SessionManager.sessionExpired.collect {
                _authState.value = AuthState.LogOut(false)
            }
        }
    }

    /** Check for a persisted token on startup */
    fun login() {
        viewModelScope.launch {
            try {
                val hasToken = service.loadToken()
                if (hasToken) connectWS()
                _authState.value = if (hasToken) AuthState.Success else AuthState.Idle
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = AuthState.Idle
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    /** Authenticate with code + password */
    fun login(code: String, password: String) {
        if (code.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error(Strings.t("login.error.empty_fields"))
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                service.requestToken(code = code, password = password)
                connectWS()
                _authState.value = AuthState.Success
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = AuthState.Error(
                    when {
                        e.message?.contains("Invalid credentials") == true ->
                            Strings.t("login.error.invalid_credentials")

                        e.message?.contains("Unable to resolve host") == true ||
                                e.message?.contains("UnknownHostException") == true ->
                            Strings.t("login.error.connection")

                        else -> Strings.t("login.error.common")
                    }
                )
            }
        }
    }

    fun connectWS() {
        viewModelScope.launch {
            service.connectBS()
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                service.logout()
            } finally {
                _authState.value = AuthState.LogOut(false)
            }
        }
    }
}
