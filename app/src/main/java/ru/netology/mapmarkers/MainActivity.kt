package ru.netology.mapmarkers

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.PointF
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapObjectCollection
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.user_location.UserLocationLayer
import com.yandex.runtime.image.ImageProvider
import ru.netology.mapmarkers.databinding.ActivityMainBinding
import ru.netology.mapmarkers.di.DependencyContainer
import ru.netology.mapmarkers.listeners.MapCameraListener
import ru.netology.mapmarkers.listeners.MapInputListener
import ru.netology.mapmarkers.listeners.MapLocationListener


class MainActivity : AppCompatActivity() {
    private val container = DependencyContainer.getInstance()

    private var locationPermission = false
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                locationPermission = true
            } else {
                showPermissionSnackbar()
            }
        }

    lateinit var binding: ActivityMainBinding


    val viewModel: MainViewModel by viewModels {
        ViewModelFactory(container.repository)
    }


    private var userLocation = Point(0.0, 0.0)
    private lateinit var mapObjectCollection: MapObjectCollection
    private lateinit var userLocationLayer: UserLocationLayer
    private val mapLocationListener = MapLocationListener(this, this)
    private val cameraListener = MapCameraListener(this)
    private val mapInputListener = MapInputListener(this, this)
    private var firstTimePlacingMarkers = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        checkMapPermission()
        MapKitFactory.initialize(this)

        setContentView(binding.root)

        setUpMap()
        subscribe()
    }

    private fun setUpMap() {
        val mapKit = MapKitFactory.getInstance()
        mapObjectCollection = binding.mapView.map.mapObjects.addCollection()
        userLocationLayer = mapKit.createUserLocationLayer(binding.mapView.mapWindow)
        userLocationLayer.isVisible = true
        userLocationLayer.isHeadingEnabled = false

        userLocationLayer.setObjectListener(mapLocationListener)
        binding.mapView.map.apply {
            addCameraListener(cameraListener)
            addInputListener(mapInputListener)
            //addTapListener()
        }

    }

    private fun subscribe() {
        binding.apply {
            findMyLocationFab.setOnClickListener {
                if (locationPermission) {
                    cameraToUserPosition()
                    cameraListener.followUserLocation = true
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
            headingFab.setOnClickListener {
                userLocationLayer.isHeadingEnabled = !userLocationLayer.isHeadingEnabled
            }
            northFab.setOnClickListener {
                val cameraPosition = binding.mapView.map.cameraPosition
                moveMap(cameraPosition.target, cameraPosition.zoom, 0f, 0f)
            }
            placeListFab.setOnClickListener {
                placesView.isVisible = !placesView.isVisible
            }
        }

        val adapter = PlacesAdapter(this)//, places)
        binding.placesView.adapter = adapter

        viewModel.data.observe(this) { places ->
            adapter.submitList(places)
            if (firstTimePlacingMarkers) {
                places.forEach { addMarker(it.point) }
                firstTimePlacingMarkers = false
            }
        }

    }

    fun moveMap(target: Point, zoom: Float = 16f, azimuth: Float = 0f, tilt: Float = 0f) {
        binding.mapView.map.move(
            CameraPosition(target, zoom, azimuth, tilt),
            Animation(Animation.Type.SMOOTH, 3f),
            null
        )
    }

    fun addPlace(target: Point) {
        val alertDialog: AlertDialog = this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage(getString(R.string.dialog_new_place))
                val textInput = makeTextInput()
                setView(textInput)
                setPositiveButton(
                    "ok"
                ) { dialog, id ->
                    viewModel.save(target, textInput.text.toString())
                    addMarker(target)
                }
                setNegativeButton(
                    "no"
                ) { dialog, id ->
                }
            }
            builder.create()
        }
        alertDialog.show()
    }

    fun makeTextInput(): TextInputEditText = TextInputEditText(this).apply {
        setText(R.string.new_place)
    }


    private fun addMarker(
        target: Point,
        pinGraphic: String = "location_pin.png",
        userData: Any? = null
    ): PlacemarkMapObject {
        val marker = mapObjectCollection.addPlacemark(
            target,
            ImageProvider.fromAsset(this, pinGraphic)
        )
        marker.userData = userData
        //markerTapListener?.let { marker.addTapListener(it) }
        return marker
    }

    private fun cameraToUserPosition() {
        if (userLocationLayer.cameraPosition() != null) {
            userLocation = userLocationLayer.cameraPosition()!!.target
            moveMap(userLocation)
        } else {
            Snackbar.make(
                binding.mapView,
                getString(R.string.no_user_location_error),
                Snackbar.LENGTH_LONG
            )
                .setAction(getString(R.string.retry)) {
                    cameraToUserPosition()
                }
                .show()
        }
    }

    private fun checkMapPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                locationPermission = true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                showPermissionSnackbar()
            }
            else -> requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    fun setAnchor() {
        userLocationLayer.setAnchor(
            PointF(
                (binding.mapView.width() * 0.5).toFloat(),
                (binding.mapView.height() * 0.5).toFloat()
            ),
            PointF(
                (binding.mapView.width() * 0.5).toFloat(),
                (binding.mapView.height() * 0.83).toFloat()
            )
        )
        cameraListener.followUserLocation = false
    }

    fun noAnchor() {
        userLocationLayer.resetAnchor()
    }

    override fun onStop() {
        binding.mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        binding.mapView.onStart()
    }

    private fun showPermissionSnackbar() {
        Snackbar.make(binding.mapView, getString(R.string.need_geolocation), Snackbar.LENGTH_LONG)
            .setAction(getString(R.string.permission)) {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            .show()
    }
}