package com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.OrderItemResponse
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.OrderItem

/**
 * Extension of the [OrderItemDto] model to facilitate conversion to an [OrderItem] domain object.
 *
 * This function maps individual line items from their data transfer format (DTO)
 * into a domain model usable by the business logic.
 *
 * @return An [OrderItem] instance with the corresponding item details.
 */
fun OrderItemResponse.toOrderItem(): OrderItem {
    return OrderItem(
        id = this.orderItemId,
        dishId = this.dishId,
        dishName = this.dishName,
        notes = this.itemNotes,
        unitPrice = this.orderItemPrice
    )
}