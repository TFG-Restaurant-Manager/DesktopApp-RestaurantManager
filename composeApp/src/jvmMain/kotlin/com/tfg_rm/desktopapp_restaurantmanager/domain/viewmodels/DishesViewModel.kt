package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.network.SessionManager
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Dishes
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Ingredient
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.DishesService
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.components.UiState
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.channels.UnresolvedAddressException
import kotlin.coroutines.cancellation.CancellationException

class DishesViewModel(
    private val service: DishesService
) : ViewModel() {


    private val _dishes = MutableStateFlow<UiState<List<Dishes>>>(UiState.Idle)
    val dishes: StateFlow<UiState<List<Dishes>>> = _dishes.asStateFlow()

    private val _availableIngredients = MutableStateFlow<UiState<List<Ingredient>>>(UiState.Idle)
    val availableIngredients: StateFlow<UiState<List<Ingredient>>> = _availableIngredients.asStateFlow()

    init {
        viewModelScope.launch {
            SessionManager.sessionExpired.collect {
                resetState()
            }
        }
    }

    fun resetState() {
        _dishes.value = UiState.Idle
    }

    fun loadRole(): String? =
        service.loadRole()

    fun loadDishes() {
        _dishes.value = UiState.Loading
        _availableIngredients.value = UiState.Loading
        viewModelScope.launch {
            try {
                val resultDishes = service.getDishes()
                val resultIngredients = service.getIngredients()
                observeSocketMessages()
                _dishes.value = UiState.Success(resultDishes)
                _availableIngredients.value = UiState.Success(resultIngredients)
            } catch (_: UnresolvedAddressException) {
                _dishes.value = UiState.Error(Strings.t("errors.ipadressnotexist"))
                _availableIngredients.value = UiState.Error(Strings.t("errors.ipadressnotexist"))
                println("Error on loadDishes in DishesViewModel, direccion ip no existente")
            } catch (e: Exception) {
                _dishes.value = UiState.Error(Strings.t("errors.undefined"))
                _availableIngredients.value = UiState.Error(Strings.t("errors.undefined"))
                e.printStackTrace()
                println("Error on loadDishes in DishesViewModel")
            }
        }
    }

    fun addDish(dish: Dishes) {
        viewModelScope.launch {
            try {
                service.addDish(dish)
                _dishes.update { state ->
                    if (state is UiState.Success) {
                        UiState.Success(state.data + dish)
                    } else state
                }
            } catch (_: UnresolvedAddressException) {
                println("Error on addDish in DishesViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on addDish in DishesViewModel")
            }
        }
    }

    fun updateDish(dish: Dishes) {
        viewModelScope.launch {
            try {
                service.updateDish(dish)
                _dishes.update { state ->
                    if (state is UiState.Success) {
                        UiState.Success(state.data.map { currentDish ->
                            if (currentDish.id == dish.id) {
                                dish
                            } else {
                                currentDish
                            }
                        })
                    } else state
                }
            } catch (_: UnresolvedAddressException) {
                println("Error on updateDish in DishesViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on updateDish in DishesViewModel")
            }
        }
    }

    fun deleteDish(id: Int) {
        viewModelScope.launch {
            try {
                service.deleteDish(id)
                _dishes.update { state ->
                    if (state is UiState.Success) {
                        UiState.Success(state.data.filter { it.id != id })
                    } else state
                }
            } catch (_: UnresolvedAddressException) {
                println("Error on deleteDish in DishesViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on deleteDish in DishesViewModel")
            }
        }
    }

    private fun observeSocketMessages() {
        viewModelScope.launch {
            try {
                service.observeMessages().collect { message ->
                    println("Mensaje recibido en DishesViewModel: $message")
                }
            } catch (_: CancellationException) {
                service.disconnectWS()
            } catch (e: Exception) {
                e.printStackTrace()
                println(e.message)
            }
        }
    }
}
