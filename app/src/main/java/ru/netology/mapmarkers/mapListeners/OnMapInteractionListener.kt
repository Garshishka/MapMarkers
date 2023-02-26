package ru.netology.mapmarkers.mapListeners

import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.PlacemarkMapObject

interface OnMapInteractionListener {

    fun onMapLongClick(point: Point){}

    fun removeMapObject(mapObject: PlacemarkMapObject){}

    fun onMarkClick(id: Long, point: Point){}
}