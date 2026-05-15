package com.tfg_rm.desktopapp_restaurantmanager.data.remote.dto

import java.time.LocalTime

data class ClipboardShift(
    val dayOffset: Long,
    val startTime: LocalTime,
    val endTime: LocalTime
)