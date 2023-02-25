package ru.netology.mapmarkers.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yandex.mapkit.geometry.Point

@Entity
data class PlaceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val latitude: Double,
    val longitude: Double,
    val name: String
) {
    fun toDto() = PlaceObject(id, Point(latitude, longitude), name)

    companion object {
        fun fromDto(dto: PlaceObject) =
            PlaceEntity(dto.id, dto.point.latitude, dto.point.longitude, dto.name)
    }
}