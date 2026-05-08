package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.EconomyService
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.nio.channels.UnresolvedAddressException

class EconomyViewModel(
    val service: EconomyService
) : ViewModel() {
    private val _title = MutableStateFlow(Strings.t("screen.economy.title"))
    val title: StateFlow<String> = _title.asStateFlow()

    fun loadEconomy() {
        viewModelScope.launch {
            try {
                service.loadInitialData()
            }catch (e: UnresolvedAddressException) {
                println("Error on loadEconomy in EconomyViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on loadEconomy in EconomyViewModel")
            }
        }
    }

    fun clear() {

    }
}
