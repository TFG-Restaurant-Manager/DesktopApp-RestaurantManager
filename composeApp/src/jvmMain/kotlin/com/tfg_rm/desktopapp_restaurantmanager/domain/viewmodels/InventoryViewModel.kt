package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.IngredientsService
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Ingredient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val service: IngredientsService
) : ViewModel() {

    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredients: StateFlow<List<Ingredient>> = _ingredients.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    fun loadInventory() {
        viewModelScope.launch {
            _ingredients.value = service.getIngredients()
            _categories.value = service.getCategories()
        }
    }

    fun addIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            service.addIngredient(ingredient)
            _ingredients.value = service.getIngredients()
        }
    }

    fun updateIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            service.updateIngredient(ingredient)
            _ingredients.value = service.getIngredients()
        }
    }

    fun deleteIngredient(id: Int) {
        viewModelScope.launch {
            service.deleteIngredient(id)
            _ingredients.value = service.getIngredients()
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            service.addCategory(name)
            _categories.value = service.getCategories()
        }
    }

    fun deleteCategory(name: String) {
        viewModelScope.launch {
            service.deleteCategory(name)
            _categories.value = service.getCategories()
        }
    }

    fun clear() {}
}
