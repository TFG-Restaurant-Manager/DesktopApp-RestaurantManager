package com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.TableOrdersResponse
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.TablesOrders

/**
 * Extension of the [TablesOrdersDto] model to facilitate conversion to a [TablesOrders] domain object.
 *
 * This function performs the deep mapping of the combined data, including the nested
 * conversion of order items using [toOrderItem].
 *
 * @return A [TablesOrders] instance with the processed and mapped data.
 */
fun TableOrdersResponse.toTablesOrders(): TablesOrders {
    return TablesOrders(
        tableId = this.tableId,
        capacity = this.capacity,
        posX = this.posX,
        posY = this.posY,
        status = this.status,
        sectionTitle = this.sectionTitle,
        orderId = this.orderId,
        orderStatus = this.orderStatus,
        orderTotal = this.orderTotal,
        orderNotes = this.orderNotes,
        orderCreatedAt = this.orderCreatedAt,
        orderItems = this.orderItems?.map { it.toOrderItem() }
    )
}