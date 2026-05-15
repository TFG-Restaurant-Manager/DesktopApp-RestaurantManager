package com.tfg_rm.desktopapp_restaurantmanager.data.remote.mapper

import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.TableCreateRequest
import com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto.TableOrdersResponse
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Section
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.Table
import com.tfg_rm.desktopapp_restaurantmanager.domain.models.TablesOrders

/**
 * Extension of the [TableOrdersResponse] model to facilitate conversion to a [TablesOrders] domain object.
 *
 * This function performs the deep mapping of the combined data, including the nested
 * conversion of order items using [toOrderItem].
 *
 * @return A [TablesOrders] instance with the processed and mapped data.
 */
fun TableOrdersResponse.toTablesOrders(): TablesOrders {
    return TablesOrders(
        tableId = this.tableId,
        tableName = this.tableName,
        capacity = this.capacity,
        posX = this.posX,
        posY = this.posY,
        status = this.status,
        sectionTitle = this.sectionTitle,
        sectionId = this.sectionId,
        orderId = this.orderId,
        orderStatus = this.orderStatus,
        orderTotal = this.orderTotal,
        orderNotes = this.orderNotes,
        orderCreatedAt = this.orderCreatedAt,
        orderItems = this.orderItems?.map { it.toOrderItem() }
    )
}

fun Table.toTableCreateRequest(): TableCreateRequest =
    TableCreateRequest(
        tableId = this.id,
        tableName = this.name,
        capacity = this.capacity,
        posX = this.posX,
        posY = this.posY,
        sectionId = this.section.id,
        sectionName = this.section.name
    )

fun TablesOrders.toTable(): Table =
    Table(
        id = this.tableId,
        name = this.tableName,
        capacity = this.capacity,
        section = Section(this.sectionId, this.sectionTitle),
        posX = this.posX.toInt(),
        posY = this.posY.toInt(),
        status = this.status
    )