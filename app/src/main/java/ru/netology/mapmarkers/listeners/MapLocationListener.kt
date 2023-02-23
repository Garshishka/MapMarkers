package ru.netology.mapmarkers.listeners

import android.content.Context
import android.graphics.Color
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider
import ru.netology.mapmarkers.MainActivity
import ru.netology.mapmarkers.R

class MapLocationListener(val context: Context, val mainActivity: MainActivity) :
    UserLocationObjectListener {

    override fun onObjectAdded(userLocationView: UserLocationView) {
        mainActivity.setAnchor()

        userLocationView.pin.setIcon(
            ImageProvider.fromResource(
                context,
                R.drawable.baseline_person_pin_circle_32
            )
        )
        userLocationView.arrow.setIcon(
            ImageProvider.fromResource(
                context,
                R.drawable.baseline_person_pin_circle_32
            )
        )
        userLocationView.accuracyCircle.fillColor = Color.BLUE
    }

    override fun onObjectRemoved(p0: UserLocationView) {}

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) {}
}