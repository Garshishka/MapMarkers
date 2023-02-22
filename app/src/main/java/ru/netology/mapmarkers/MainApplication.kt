package ru.netology.mapmarkers

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class MainApplication: Application() {

    override fun onCreate() {
        MapKitFactory.setApiKey(API_KEY)
        super.onCreate()
    }
}