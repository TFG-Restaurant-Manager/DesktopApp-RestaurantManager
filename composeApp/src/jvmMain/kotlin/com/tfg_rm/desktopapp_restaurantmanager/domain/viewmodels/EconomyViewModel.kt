package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.EconomyService
import kotlinx.coroutines.launch
import java.nio.channels.UnresolvedAddressException

class EconomyViewModel(
    val service: EconomyService
) : ViewModel() {

    fun loadEconomy() {
        viewModelScope.launch {
            try {
                service.loadInitialData()
            } catch (e: UnresolvedAddressException) {
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
