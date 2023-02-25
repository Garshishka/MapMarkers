package ru.netology.mapmarkers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.netology.mapmarkers.data.PlacesRepository

class ViewModelFactory(private val repository: PlacesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(MainViewModel::class.java) ->
                return MainViewModel(repository = repository) as T
            else ->
                throw java.lang.IllegalArgumentException("unknown ViewModel class ${modelClass.name}")
        }
    }
}