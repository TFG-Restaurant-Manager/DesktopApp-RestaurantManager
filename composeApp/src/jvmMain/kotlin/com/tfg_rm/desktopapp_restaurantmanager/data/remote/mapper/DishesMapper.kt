package com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.DishCreateRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.DishIngredientRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.DishesResponse
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.DishesUpdateRequest
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Category
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.DishIngredient
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Dishes

fun DishesResponse.toDishes(): Dishes {
    return Dishes(
        id = this.id.toInt(),
        name = this.name,
        description = this.description,
        category = Category(this.categoryId, this.categoryName),
        price = this.price,
        available = this.available
    )
}

fun Dishes.toDishesResponse(): DishesResponse {
    return DishesResponse(
        id = this.id.toLong(),
        name = this.name,
        description = this.description,
        categoryName = this.category.name,
        categoryId = this.category.id,
        price = this.price,
        available = this.available,
        ingredients = this.ingredients.map { it.toDishIngredientRequest() }
    )
}

fun Dishes.toDishCreateRequest(): DishCreateRequest {
    return DishCreateRequest(
        name = this.name,
        description = this.description!!,
        categoryId = this.category.id,
        price = this.price,
        available = this.available,
        restaurantId = this.restaurantId,
        ingredients = this.ingredients.map { it.toDishIngredientRequest() }
    )
}

fun DishIngredient.toDishIngredientRequest(): DishIngredientRequest {
    return DishIngredientRequest(
        ingredient = this.ingredient.toIngredientsDto(),
        quantity = this.quantity
    )
}

fun Dishes.toDishesUpdateRequest(): DishesUpdateRequest {
    return DishesUpdateRequest(
        name = this.name,
        description = this.description!!,
        price = this.price,
        available = this.available,
        ingredients = this.ingredients.map { it.toDishIngredientRequest() }
    )
}