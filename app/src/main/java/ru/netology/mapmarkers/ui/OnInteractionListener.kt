package ru.netology.mapmarkers.ui

import ru.netology.mapmarkers.data.PlaceObject

interface OnInteractionListener {
    fun onPlaceClick(place: PlaceObject){}

    fun onEditClick(place: PlaceObject){}

    fun onDeleteClick(place: PlaceObject){}
}