package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Dishes
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Ingredient
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.DishesService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DishesViewModel(
    private val service: DishesService
) : ViewModel() {

    private val _dishes = MutableStateFlow<List<Dishes>>(emptyList())
    val dishes: StateFlow<List<Dishes>> = _dishes.asStateFlow()

    private val _availableIngredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val availableIngredients: StateFlow<List<Ingredient>> = _availableIngredients.asStateFlow()

    fun loadDishes() {
        viewModelScope.launch {
            _dishes.value = service.getDishes()
            _availableIngredients.value = service.getIngredients()
        }
    }

    fun addDish(dish: Dishes) {
        viewModelScope.launch {
            service.addDish(dish)
            _dishes.value = service.getDishes()
        }
    }

    fun updateDish(dish: Dishes) {
        viewModelScope.launch {
            service.updateDish(dish)
            _dishes.value = service.getDishes()
        }
    }

    fun deleteDish(id: Int) {
        viewModelScope.launch {
            service.deleteDish(id)
            _dishes.value = service.getDishes()
        }
    }
}
