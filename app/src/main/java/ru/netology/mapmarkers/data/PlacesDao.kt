package ru.netology.mapmarkers.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PlacesDao {
    @Query("SELECT * FROM PlaceEntity ORDER BY id DESC")
    fun getAll(): LiveData<List<PlaceEntity>>

    @Query("SELECT * FROM PlaceEntity ORDER BY id DESC")
    fun getNewest(): PlaceEntity

    @Query("SELECT * FROM PlaceEntity WHERE id = :id")
    fun findById(id: Long) : PlaceEntity

    @Insert
    fun insert(place: PlaceEntity)

    @Query("UPDATE PlaceEntity SET name = :name WHERE id = :id")
    fun updateContentByID(id: Long, name: String)

    fun save(place: PlaceEntity) =
        if (place.id == 0L) insert(place) else updateContentByID(place.id, place.name)

    @Query("DELETE FROM PlaceEntity WHERE id = :id")
    fun deleteById(id: Long)
}