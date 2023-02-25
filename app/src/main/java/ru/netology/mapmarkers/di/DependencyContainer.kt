package ru.netology.mapmarkers.di

import android.content.Context
import androidx.room.Room
import ru.netology.mapmarkers.data.AppDb
import ru.netology.mapmarkers.data.PlacesRepository

class DependencyContainer private constructor(context: Context) {

    companion object {
        @Volatile
        private var instance: DependencyContainer? = null

        fun initApp(context: Context) {
            instance = DependencyContainer(context)
        }

        fun getInstance(): DependencyContainer {
            return instance!!
        }
    }

    private val appDb =
        Room.databaseBuilder(context, AppDb::class.java, "app.db")
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

    private val placesDao = appDb.placesDao()

    val repository: PlacesRepository = PlacesRepository(placesDao)
}