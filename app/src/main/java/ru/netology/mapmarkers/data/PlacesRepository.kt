package ru.netology.mapmarkers.data

import androidx.lifecycle.Transformations
import com.yandex.mapkit.geometry.Point

class PlacesRepository(
    private val dao: PlacesDao
) {
    fun getAll() = Transformations.map(dao.getAll()) { list ->
        list.map {
            PlaceObject(it.id, Point(it.latitude, it.longitude), it.name)
        }
    }

    fun save(place: PlaceObject) {
        dao.save(PlaceEntity.fromDto(place))
    }

}
