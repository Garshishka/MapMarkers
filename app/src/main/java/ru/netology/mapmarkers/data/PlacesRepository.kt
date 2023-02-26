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

    fun getLatestId(): Long = dao.getNewest().id

    fun save(place: PlaceObject) {
        dao.save(PlaceEntity.fromDto(place))
    }

    fun delete(id: Long){
        dao.deleteById(id)
    }

    fun getById(id: Long): PlaceObject{
        val entity = dao.findById(id)
        return PlaceObject(entity.id, Point(entity.latitude,entity.longitude), entity.name)
    }

}
