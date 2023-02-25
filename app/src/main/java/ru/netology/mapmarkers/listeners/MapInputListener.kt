package ru.netology.mapmarkers.listeners

import android.content.Context
import android.widget.Toast
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.InputListener
import com.yandex.mapkit.map.Map
import ru.netology.mapmarkers.MainActivity

class MapInputListener(val context: Context, val mainActivity: MainActivity) : InputListener {
    override fun onMapTap(map: Map, point: Point) {
        Toast.makeText(context, "Tap ${point.latitude}-${point.longitude}", Toast.LENGTH_SHORT)
            .show()
    }

    override fun onMapLongTap(map: Map, point: Point) {
        Toast.makeText(context, "Long Tap", Toast.LENGTH_SHORT).show()
        mainActivity.addPlace(point)
    }
}