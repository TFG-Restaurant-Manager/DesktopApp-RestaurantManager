package com.tfg_rm.desktopapp_restaurantmanager.domain.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Ingredient
import com.tfg_rm.desktopapp_restaurantmanager.domain.service.IngredientsService
import com.tfg_rm.desktopapp_restaurantmanager.ui.screens.components.UiState
import com.tfg_rm.desktopapp_restaurantmanager.util.Strings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.nio.channels.UnresolvedAddressException

class InventoryViewModel(
    private val service: IngredientsService
) : ViewModel() {

    private val _ingredients = MutableStateFlow<UiState<List<Ingredient>>>(UiState.Idle)
    val ingredients: StateFlow<UiState<List<Ingredient>>> = _ingredients.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(emptyList())
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    fun resetState() {
        _ingredients.value = UiState.Idle
    }

    fun loadInventory() {
        _ingredients.value = UiState.Loading
        viewModelScope.launch {
            try {
                val response = service.getIngredients()
                _ingredients.value = UiState.Success(response)
                _categories.value = (_ingredients.value as UiState.Success).data
                    .map { it.category }.distinct()
            } catch (_: UnresolvedAddressException) {
                _ingredients.value = UiState.Error(Strings.t("errors.ipadressnotexist"))
                println("Error on loadInventory in InventoryViewModel, direccion ip no existente")
            } catch (e: Exception) {
                _ingredients.value = UiState.Error(Strings.t("errors.undefined"))
                e.printStackTrace()
                println("Error on loadInventory in InventoryViewModel")
            }
        }
    }

    fun addIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            try {
                service.addIngredient(ingredient)
                _ingredients.update { state ->
                    if (state is UiState.Success) {
                        UiState.Success(state.data + ingredient)
                    } else state
                }
            } catch (_: UnresolvedAddressException) {
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
                _ingredients.update { state ->
                    if (state is UiState.Success) {
                        UiState.Success(state.data.map {
                            if (it.id == ingredient.id) {
                                ingredient
                            } else it
                        })
                    } else state
                }
            } catch (_: UnresolvedAddressException) {
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
                _ingredients.update { state ->
                    if (state is UiState.Success) {
                        UiState.Success(state.data.filter { it.id != id })
                    } else state
                }
            } catch (_: UnresolvedAddressException) {
                println("Error on deleteIngredient in InventoryViewModel, direccion ip no existente")
            } catch (e: Exception) {
                e.printStackTrace()
                println("Error on deleteIngredient in InventoryViewModel")
            }
        }
    }
}
