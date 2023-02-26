package ru.netology.mapmarkers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.mapmarkers.data.PlaceObject
import ru.netology.mapmarkers.databinding.PlacesListLayoutBinding
import ru.netology.mapmarkers.ui.OnInteractionListener

class PlacesAdapter(
    private val onInteractionListener: OnInteractionListener,
) :
    ListAdapter<PlaceObject, PlacesViewHolder>(PlaceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val binding =
            PlacesListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacesViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        val place = getItem(position)
        holder.bind(place)
    }
}

class PlacesViewHolder(
    private val binding: PlacesListLayoutBinding,
    private val onInteractionListener: OnInteractionListener,
) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(place: PlaceObject) {
        binding.apply {
            val lat = place.point.latitude.toString().take(10) + "..."
            val long = place.point.longitude.toString().take(10) + "..."
            placeCoords.text = "$lat | $long"

            placeName.text = place.name

            placeCard.setOnClickListener {
                onInteractionListener.onPlaceClick(place)
            }
            editButton.setOnClickListener {
                onInteractionListener.onEditClick(place)
            }
            deleteButton.setOnClickListener {
                onInteractionListener.onDeleteClick(place)
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