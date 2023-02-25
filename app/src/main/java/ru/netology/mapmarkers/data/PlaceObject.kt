package ru.netology.mapmarkers.data

import com.yandex.mapkit.geometry.Point

data class PlaceObject(
    val id: Long,
    val point: Point,
    val name: String,
)
