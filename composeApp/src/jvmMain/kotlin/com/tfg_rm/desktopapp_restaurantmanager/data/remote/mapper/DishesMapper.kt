package com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.DishCreateRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.DishIngredientRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.DishesResponse
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.DishIngredient
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Dishes

fun DishesResponse.toDishes(): Dishes {
    return Dishes(
        id = this.id.toInt(),
        name = this.name,
        description = this.description,
        categoryName = this.categoryName,
        price = this.price,
        available = this.available
    )
}

fun Dishes.toDishesResponse(): DishesResponse {
    return DishesResponse(
        id = this.id.toLong(),
        name = this.name,
        description = this.description,
        categoryName = this.categoryName,
        price = this.price,
        available = this.available
    )
}

fun Dishes.toDishCreateRequest(): DishCreateRequest {
    return DishCreateRequest(
        name = this.name,
        description = this.description!!,
        categoryName = this.categoryName!!,
        price = this.price,
        available = this.available,
        restaurantId = this.restaurantId,
        ingredients = this.ingredients.map { it.toDishIngredientRequest() }
    )
}

fun DishIngredient.toDishIngredientRequest(): DishIngredientRequest {
    return DishIngredientRequest(
        ingredientId = this.ingredient.id,
        quantity = this.quantity
    )
}