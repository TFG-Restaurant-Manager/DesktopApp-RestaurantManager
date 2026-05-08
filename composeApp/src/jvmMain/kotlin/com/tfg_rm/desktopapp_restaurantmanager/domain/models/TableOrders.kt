package com.tfg_rm.desktopapp_restaurantmanager.domain.models

/**
 * Internal domain model representing a unified view of a restaurant table and its active order.
 *
 * @property tableId Unique identifier of the table.
 * @property capacity Seating capacity of the table.
 * @property posX X-coordinate for UI positioning.
 * @property posY Y-coordinate for UI positioning.
 * @property status Current occupancy status of the table.
 * @property sectionTitle Name of the area where the table is located.
 * @property orderId ID of the active order, or null if empty.
 * @property orderStatus Status of the order (e.g., "PENDING").
 * @property orderTotal Total amount of the current ticket.
 * @property orderNotes General comments regarding the order.
 * @property orderCreatedAt Timestamp of the order creation.
 * @property orderItems List of products included in the order.
 */
data class TablesOrders(
    val tableId: Int,
    val tableName: String,
    val capacity: Int,
    val posX: Double,
    val posY: Double,
    val status: String,
    val sectionTitle: String,
    val sectionId: Int,
    val orderId: Int?,
    val orderStatus: String?,
    val orderTotal: Double?,
    val orderNotes: String?,
    val orderCreatedAt: String?,
    val orderItems: List<OrderItem>?
)