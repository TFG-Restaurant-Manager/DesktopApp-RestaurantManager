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
        _dishes.update {currentList ->
            currentList + dish
        }
        viewModelScope.launch {
            service.addDish(dish)
        }
    }

    fun updateDish(dish: Dishes) {
        _dishes.value.map {currentDish ->
            if (currentDish.id == dish.id) {
                dish
            }else {
                currentDish
            }
        }
        viewModelScope.launch {
            service.updateDish(dish)
        }
    }

    fun deleteDish(id: Int) {
        _dishes.value.map { it.id != id }
        viewModelScope.launch {
            service.deleteDish(id)
        }
    }
}
