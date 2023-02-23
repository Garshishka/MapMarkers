package ru.netology.mapmarkers.listeners

import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.Map
import ru.netology.mapmarkers.MainActivity

class MapCameraListener(val mainActivity: MainActivity) : CameraListener {
    var followUserLocation = false

    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateReason,
        finish: Boolean
    ) {
        if (finish) {
            if (followUserLocation) {
                mainActivity.setAnchor()
            }
        } else {
            if (!followUserLocation) {
                mainActivity.noAnchor()
            }
        }
    }
}
