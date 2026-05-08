package com.tfg_rm.desktopapp_restaurantmanager.domain

enum class OrderType(val label: String) {
    TABLE("Mesa"),
    PICKUP("Recoger"),
    DELIVERY("Domicilio")
}
