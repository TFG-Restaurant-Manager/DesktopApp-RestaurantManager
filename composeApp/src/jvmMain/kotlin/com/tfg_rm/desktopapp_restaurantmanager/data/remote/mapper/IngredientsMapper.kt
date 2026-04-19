package com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.IngredientOperationRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.IngredientsDto
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Ingredient

fun IngredientsDto.toIngredient(): Ingredient {
    return Ingredient(
        id = this.id,
        name = this.name,
        unit = this.unit,
        stockQuantity = this.stockQuantity,
        costUnit = this.costUnit,
        category = this.category,
        minimumStock = this.minimumStock,
        usableInDishes = true
    )
}

fun Ingredient.toIngredientOperationRequest(): IngredientOperationRequest =
    IngredientOperationRequest(
        name = this.name,
        unit = this.unit,
        costUnit = this.costUnit,
        minimumStock = this.minimumStock,
        categoryId = null// It Misses the category id
    )