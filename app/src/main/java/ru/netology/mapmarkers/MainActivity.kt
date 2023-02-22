package ru.netology.mapmarkers

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView


class MainActivity : AppCompatActivity() {
    private var mapview: MapView? = null
    private val TARGET_LOCATION: Point = Point(59.845933, 30.320045)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)

        setContentView(R.layout.activity_main)
        mapview = findViewById(R.id.map)

        initialMap()
    }

    private fun initialMap() {
        mapview?.map?.move(
            CameraPosition(TARGET_LOCATION, 16f, 0f, 0f),
            Animation(Animation.Type.SMOOTH, 5f),
            null
        )
    }


    override fun onStop() {
        mapview?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapview?.onStart()
    }
}