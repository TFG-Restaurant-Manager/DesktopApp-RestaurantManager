package com.tfg_rm.desktopapp_restaurantmanager.domain

enum class OrderType(val label: String) {
    TABLE("Mesa"),
    TAKEAWAY("Para llevar"),
    PICKUP("Recoger"),
    DELIVERY("Domicilio")
}
