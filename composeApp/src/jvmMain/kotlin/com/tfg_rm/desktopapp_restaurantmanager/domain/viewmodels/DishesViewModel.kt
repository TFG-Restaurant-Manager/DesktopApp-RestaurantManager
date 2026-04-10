package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Dishes
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Ingredient
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.DishesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.channels.UnresolvedAddressException

class DishesViewModel(
    private val service: DishesService
) : ViewModel() {

    private val _dishes = MutableStateFlow<List<Dishes>>(emptyList())
    val dishes: StateFlow<List<Dishes>> = _dishes.asStateFlow()

    private val _availableIngredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val availableIngredients: StateFlow<List<Ingredient>> = _availableIngredients.asStateFlow()

    fun loadDishes() {
        viewModelScope.launch {
            try {
                _dishes.value = service.getDishes()
                _availableIngredients.value = service.getIngredients()
            } catch (e: UnresolvedAddressException) {
                println("Error on loadDishes in DishesViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on loadDishes in DishesViewModel")
            }
        }
    }

    fun addDish(dish: Dishes) {
        viewModelScope.launch {
            try {
                service.addDish(dish)
                _dishes.update { currentList ->
                    currentList + dish
                }
            } catch (e: UnresolvedAddressException) {
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
                _dishes.value.map { currentDish ->
                    if (currentDish.id == dish.id) {
                        dish
                    } else {
                        currentDish
                    }
                }
            } catch (e: UnresolvedAddressException) {
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
                _dishes.value.map { it.id != id }
            } catch (e: UnresolvedAddressException) {
                println("Error on deleteDish in DishesViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on deleteDish in DishesViewModel")
            }
        }
    }
}
