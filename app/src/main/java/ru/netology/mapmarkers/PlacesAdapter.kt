package ru.netology.mapmarkers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.mapmarkers.data.PlaceObject
import ru.netology.mapmarkers.databinding.PlacesListLayoutBinding

class PlacesAdapter(val mainActivity: MainActivity) ://, private val places: List<PlaceObject>) :
    ListAdapter<PlaceObject, PlacesViewHolder>(PlaceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val binding =
            PlacesListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacesViewHolder(mainActivity, binding)
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        val place = getItem(position)
        holder.bind(place)
    }
}

class PlacesViewHolder(
    val mainActivity: MainActivity,
    private val binding: PlacesListLayoutBinding
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(place: PlaceObject) {
        binding.apply {
            val lat = place.point.latitude.toString().take(10) + "..."
            val long = place.point.longitude.toString().take(10) + "..."
            placeCoords.text = "$lat and $long"

            placeName.text = place.name

            placeCard.setOnClickListener {
                mainActivity.moveMap(place.point)
            }
        }
    }
}

class PlaceDiffCallback : DiffUtil.ItemCallback<PlaceObject>() {
    override fun areItemsTheSame(oldItem: PlaceObject, newItem: PlaceObject): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: PlaceObject, newItem: PlaceObject): Boolean {
        return oldItem == newItem
    }
}