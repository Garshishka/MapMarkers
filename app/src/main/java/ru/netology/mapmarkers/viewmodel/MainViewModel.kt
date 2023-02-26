package ru.netology.mapmarkers.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yandex.mapkit.geometry.Point
import ru.netology.mapmarkers.data.PlaceObject
import ru.netology.mapmarkers.data.PlacesRepository

private val empty = PlaceObject(
    0, Point(0.0, 0.0), "no name"
)

class MainViewModel(private val repository: PlacesRepository) : ViewModel() {
    val data = repository.getAll()
    val edited = MutableLiveData(empty)

    fun empty() {
        edited.value = empty
    }

    fun save(point: Point, name: String, id: Long? = null): Long {
        edited.value?.let {
            edited.value = it.copy(id=id ?: 0L, point = point, name = name)
        }
        edited.value?.let {
            repository.save(it)
        }
        empty()
        return id ?: repository.getLatestId()
    }

    fun delete(id: Long){
        repository.delete(id)
    }

    fun getById(id: Long) = repository.getById(id)
}