package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Ingredient
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.IngredientsService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.nio.channels.UnresolvedAddressException

class InventoryViewModel(
    private val service: IngredientsService
) : ViewModel() {

    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredients: StateFlow<List<Ingredient>> = _ingredients.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    fun loadInventory() {
        viewModelScope.launch {
            try {
                _ingredients.value = service.getIngredients()
                _categories.value = _ingredients.value.map { it.category }.distinct()
            } catch (e: UnresolvedAddressException) {
                println("Error on loadInventory in InventoryViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on loadInventory in InventoryViewModel")
            }
        }
    }

    fun addIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            try {
                service.addIngredient(ingredient)
                _ingredients.value = service.getIngredients()
            } catch (e: UnresolvedAddressException) {
                println("Error on addIngredient in InventoryViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on addIngredient in InventoryViewModel")
            }
        }
    }

    fun updateIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            try {
                service.updateIngredient(ingredient)
                _ingredients.value = service.getIngredients()
            } catch (e: UnresolvedAddressException) {
                println("Error on updateIngredient in InventoryViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on updateIngredient in InventoryViewModel")
            }
        }
    }

    fun deleteIngredient(id: Int) {
        viewModelScope.launch {
            try {
                service.deleteIngredient(id)
                _ingredients.value = service.getIngredients()
            } catch (e: UnresolvedAddressException) {
                println("Error on deleteIngredient in InventoryViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on deleteIngredient in InventoryViewModel")
            }
        }
    }

    fun clear() {}
}
