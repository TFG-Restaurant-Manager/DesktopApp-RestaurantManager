package com.tfg_rm.desktopapp_restaurantmanager

class JVMPlatform {
    val name: String = "Java ${System.getProperty("java.version")}"
}

fun getPlatform() = JVMPlatform()