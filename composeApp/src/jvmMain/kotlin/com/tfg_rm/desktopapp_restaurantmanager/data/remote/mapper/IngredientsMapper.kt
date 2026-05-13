package com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.IngredientOperationRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.IngredientsDto
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Category
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Ingredient

fun IngredientsDto.toIngredient(): Ingredient {
    return Ingredient(
        id = this.id,
        name = this.name,
        unit = this.unit,
        stockQuantity = this.stockQuantity,
        costUnit = this.costUnit,
        category = Category(this.categoryId, this.category),
        minimumStock = this.minimumStock,
        usableInDishes = true
    )
}

fun Ingredient.toIngredientsDto(): IngredientsDto {
    return IngredientsDto(
        id = this.id,
        name = this.name,
        unit = this.unit,
        stockQuantity = this.stockQuantity,
        costUnit = this.costUnit,
        minimumStock = this.minimumStock,
        category = this.category.name,
        categoryId = this.category.id,
    )
}

fun Ingredient.toIngredientOperationRequest(): IngredientOperationRequest =
    IngredientOperationRequest(
        name = this.name,
        unit = this.unit,
        stockQuantity = this.stockQuantity,
        costUnit = this.costUnit,
        minimumStock = this.minimumStock,
        categoryId = this.category.id,
        categoryName = this.category.name
    )