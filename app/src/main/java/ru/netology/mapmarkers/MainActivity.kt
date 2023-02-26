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
import ru.netology.mapmarkers.data.PlaceObject
import ru.netology.mapmarkers.databinding.ActivityMainBinding
import ru.netology.mapmarkers.di.DependencyContainer
import ru.netology.mapmarkers.mapListeners.*
import ru.netology.mapmarkers.ui.OnInteractionListener
import ru.netology.mapmarkers.viewmodel.MainViewModel
import ru.netology.mapmarkers.viewmodel.ViewModelFactory


class MainActivity : AppCompatActivity() {
    //Dependency part
    private val container = DependencyContainer.getInstance()

    //Permission part
    private var locationPermission = false
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                locationPermission = true
            } else {
                showPermissionSnackbar()
            }
        }

    //UI and logic part
    lateinit var binding: ActivityMainBinding
    val viewModel: MainViewModel by viewModels {
        ViewModelFactory(container.repository)
    }
    private val onInteractionListener = object : OnInteractionListener {

        override fun onPlaceClick(place: PlaceObject) {
            moveMap(place.point)
        }

        override fun onEditClick(place: PlaceObject) {
            renamePlace(place)
        }

        override fun onDeleteClick(place: PlaceObject) {
            deletePlace(place)
        }

    }
    private val onMapInteractionListener = object : OnMapInteractionListener {
        override fun onMapLongClick(point: Point) {
            addPlace(point)
        }

        override fun removeMapObject(mapObject: PlacemarkMapObject) {
            mapObjectCollection.remove(mapObject)
        }

        override fun onMarkClick(id: Long, point: Point) {
            val place = viewModel.getById(id)
            moveMap(place.point)
            interactionWithMark(place)
        }

        override fun setAnchor() {
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
        }

        override fun noAnchor() {
            userLocationLayer.resetAnchor()
            userLocationLayer.isHeadingEnabled = false
        }
    }

    //Map part
    private var userLocation = Point(0.0, 0.0)
    private lateinit var mapObjectCollection: MapObjectCollection
    private lateinit var userLocationLayer: UserLocationLayer
    private val cameraListener = MapCameraListener(onMapInteractionListener)
    private val mapInputListener = MapInputListener(onMapInteractionListener)
    private var markerTapListener = PlaceTapListener(onMapInteractionListener)
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

        binding.mapView.map.apply {
            addCameraListener(cameraListener)
            addInputListener(mapInputListener)
        }
    }

    private fun subscribe() {
        binding.apply {
            findMyLocationFab.setOnClickListener {
                if (locationPermission) {
                    cameraToUserPosition()
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
            headingFab.setOnClickListener {
                if (locationPermission) {
                    userLocationLayer.isHeadingEnabled = true
                    cameraListener.followUserLocation = userLocationLayer.isHeadingEnabled
                    cameraToUserPosition(17f)
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }
            northFab.setOnClickListener {
                val cameraPosition = binding.mapView.map.cameraPosition
                moveMap(cameraPosition.target, cameraPosition.zoom, 0f, 0f)
            }
            placeListFab.setOnClickListener {
                placesView.isVisible = !placesView.isVisible
            }
        }

        val adapter = PlacesAdapter(onInteractionListener)
        binding.placesView.adapter = adapter

        viewModel.data.observe(this) { places ->
            adapter.submitList(places)
            if (firstTimePlacingMarkers) {
                places.forEach { addMarker(it.point,it.id) }
                firstTimePlacingMarkers = false
            }
        }
    }

    fun moveMap(target: Point, zoom: Float = 15f, azimuth: Float = 0f, tilt: Float = 0f) {
        binding.mapView.map.move(
            CameraPosition(target, zoom, azimuth, tilt),
            Animation(Animation.Type.SMOOTH, 3f),
            null
        )
    }

    //Dialog for tapping the mark: Delete or rename
    fun interactionWithMark(place: PlaceObject){
        val alertDialog: AlertDialog = this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle(place.name)
                setMessage(getString(R.string.dialog_mark_interaction))
                setPositiveButton(
                    getString(R.string.rename_place)
                ) { _, _ ->
                    renamePlace(place)
                }
                setNeutralButton(
                    getString(R.string.delete_place)
                ) {_, _ ->
                    deletePlace(place)
                }
                setNegativeButton(
                    getString(R.string.back)
                ) { _, _ ->
                }
            }
            builder.create()
        }
        alertDialog.show()
    }

    //Dialog for removing place
    fun deletePlace(place: PlaceObject) {
        val alertDialog: AlertDialog = this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage(getString(R.string.dialog_delete_place))
                setPositiveButton(
                    getString(R.string.delete_place)
                ) { _, _ ->
                    traverseMapObjectsToRemove(place.point)
                    viewModel.delete(place.id)
                }
                setNegativeButton(
                    getString(R.string.back)
                ) { _, _ ->
                }
            }
            builder.create()
        }
        alertDialog.show()
    }

    //Dialog when adding new place
    fun addPlace(target: Point) {
        val alertDialog: AlertDialog = this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage(getString(R.string.dialog_new_place))
                val textInput = makeTextInput()
                setView(textInput)
                setPositiveButton(
                    getString(R.string.add_place)
                ) { _, _ ->
                    val id = viewModel.save(target, textInput.text.toString())
                    addMarker(target, id)
                }
                setNegativeButton(
                    getString(R.string.back)
                ) { _, _ ->
                }
            }
            builder.create()
        }
        alertDialog.show()
    }

    //Dialog when renaming place
    fun renamePlace(place: PlaceObject) {
        val alertDialog: AlertDialog = this.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setMessage(getString(R.string.dialog_rename_place))
                val textInput = makeTextInput(place.name)
                setView(textInput)
                setPositiveButton(
                    getString(R.string.rename_place)
                ) { _, _ ->
                    viewModel.save(place.point, textInput.text.toString(), place.id)
                }
                setNegativeButton(
                    getString(R.string.back)
                ) { _, _ ->
                }
            }
            builder.create()
        }
        alertDialog.show()
    }

    private fun makeTextInput(oldName: String? = null): TextInputEditText =
        TextInputEditText(this).apply {
            if (oldName == null) {
                setText(R.string.new_place)
            } else {
                setText(oldName)
            }
        }

    //we save ID for place as userData on the mark
    private fun addMarker(
        target: Point,
        userData: Any? = null,
        pinGraphic: String = "location_pin.png",
    ): PlacemarkMapObject {
        val marker = mapObjectCollection.addPlacemark(
            target,
            ImageProvider.fromAsset(this, pinGraphic)
        )
        marker.userData = userData
        markerTapListener.let { marker.addTapListener(it) }
        return marker
    }

    private fun traverseMapObjectsToRemove(point: Point) {
        mapObjectCollection.traverse(RemoveMapObjectByPoint(onMapInteractionListener, point))
    }

    private fun cameraToUserPosition(zoom: Float = 16f) {
        if (userLocationLayer.cameraPosition() != null) {
            userLocation = userLocationLayer.cameraPosition()!!.target
            moveMap(userLocation, zoom)
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