package ru.netology.mapmarkers

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.layers.ObjectEvent
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.mapview.MapView
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.mapkit.user_location.UserLocationObjectListener
import com.yandex.mapkit.user_location.UserLocationView
import com.yandex.runtime.image.ImageProvider


class MainActivity : AppCompatActivity(),UserLocationObjectListener, CameraListener {
    private var locationPermission = false
    val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ isGranted ->
        if (isGranted){
            locationPermission = true
        } else{

        }
    }

    private var userLocation = Point(0.0, 0.0)
    private lateinit var mapView: MapView
    private lateinit var fab: FloatingActionButton
    private lateinit var userLocationLayer: UserLocationLayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkMapPermission()
        MapKitFactory.initialize(this)

        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.map)
        fab = findViewById(R.id.find_my_location_fab)

        setUpMap()
        subscribe()
    }

    private fun setUpMap() {
        val mapKit = MapKitFactory.getInstance()
        userLocationLayer = mapKit.createUserLocationLayer(mapView.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = true
        userLocationLayer.setObjectListener(this)

        mapView.map.addCameraListener(this)


    }

    private fun subscribe(){
        fab.setOnClickListener {
            if(locationPermission){
                cameraToUserPosition()
            }else{
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun cameraToUserPosition() {
        if(userLocationLayer.cameraPosition() != null){
            userLocation = userLocationLayer.cameraPosition()!!.target
            mapView.map.move(
                CameraPosition(userLocation,16f,0f,0f),
                Animation(Animation.Type.SMOOTH,5f),
                null
            )
        } else{
            Snackbar.make(mapView,"Can't find your location yet", Snackbar.LENGTH_LONG)
                .setAction("Retry"){
                    cameraToUserPosition()
                }
                .show()
        }
    }

    private fun checkMapPermission(){
        when{
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ->{
                locationPermission = true
            }
//            shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
//                // TODO toast
//            }
            else -> requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

    }



    override fun onObjectAdded(userLocationView: UserLocationView) {
        userLocationView.pin.setIcon(ImageProvider.fromResource(this,R.drawable.baseline_person_pin_circle_32))
        userLocationView.arrow.setIcon(ImageProvider.fromResource(this,R.drawable.baseline_person_pin_circle_32))
        userLocationView.accuracyCircle.fillColor = Color.BLUE
    }

    override fun onObjectRemoved(p0: UserLocationView) {  }

    override fun onObjectUpdated(p0: UserLocationView, p1: ObjectEvent) { }

    override fun onCameraPositionChanged(
        p0: Map,
        p1: CameraPosition,
        p2: CameraUpdateReason,
        finish: Boolean
    ) {
        if(finish){

        }
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }
}