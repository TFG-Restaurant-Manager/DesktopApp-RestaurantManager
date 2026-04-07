package com.tfg_rm.desktopapp_restaurantmanager.domain.models

/**
 * Represents a restaurant table placed on the floor-plan grid.
 * Maps to DB: tables_restaurant
 *
 * @param id           Unique identifier. Table 1 is the permanent default (cannot be moved).
 * @param restaurantId FK to restaurants.id
 * @param name         Display name (e.g. "Mesa 1").
 * @param capacity     Number of seats.
 * @param posX         1-based column position on the grid (DB: pos_x).
 * @param posY         1-based row position on the grid (DB: pos_y).
 * @param active       Whether the table is active.
 * @param sectionId    Optional FK to table_sections.
 */
data class Table(
    val id: Int,
    val restaurantId: Int = 1,
    val name: String = "",
    val capacity: Int,
    var posX: Int,
    var posY: Int,
    val active: Boolean = true,
    val sectionId: Int? = null
)
