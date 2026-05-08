package com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.*
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Order
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.OrderHistorical
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.OrderItem
import java.time.LocalDateTime

/**
 * Extension of the [OrderItemResponse] model to facilitate conversion to an [OrderItem] domain object.
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
        unitPrice = this.orderItemPrice,
        status = this.status
    )
}

fun OrderResponse.toOrder(): Order =
    Order(
        id = this.orderId,
        type = this.type,
        status = this.status,
        total = this.total,
        notes = this.notes,
        createdAt = LocalDateTime.parse(this.createdAt),
        orderItemsList = this.items.map { it.toOrderItem() } as MutableList<OrderItem>,
        pickupTime = if (this.pickupTime != null) LocalDateTime.parse(this.pickupTime) else null,
        deliveryAddress = this.deliveryAddress,
        tableId = this.tableId,
        tableName = this.tablName
    )

fun OrderResponse.toOrderHistorical(): OrderHistorical {
    return OrderHistorical(
        orderId = this.orderId,
        type = this.type,
        status = this.status,
        total = this.total,
        notes = this.notes,
        createdAt = LocalDateTime.parse(this.createdAt),
        items = this.items.map { it.toOrderItem() },
        pickupTime = if (this.pickupTime != null) LocalDateTime.parse(this.pickupTime) else null,
        deliveryAddress = this.deliveryAddress,
        tableId = this.tableId,
        tableName = this.tablName
    )
}

fun Order.toOrderCreateRequest(): OrderCreateRequest =
    OrderCreateRequest(
        payload = this.toOrderRequest()
    )

fun Order.toOrderUpdateRequest(): OrderUpdateRequest =
    OrderUpdateRequest(
        payload = this.toOrderRequest()
    )

fun Order.toOrderRequest(): OrderRequest =
    OrderRequest(
        id = id.toLong(),
        type = this.orderType,
        tableId = this.tableId?.toLong(),
        tableName = this.tableName,
        notes = this.notes,
        deliveryAddress = this.deliveryAddress,
        deliveryNotes = this.deliveryNotes,
        pickupTime = this.pickupTime?.toString(),
        createdAt = this.createdAt.toString(),
        items = this.orderItemsList.map { item -> item.toOrderItemRequest() },
        status = this.status
    )

fun OrderItem.toOrderItemRequest(): OrderItemRequest =
    OrderItemRequest(
        id = this.id.toLong(),
        dishId = this.dishId.toLong(),
        notes = this.notes,
        status = this.status
    )