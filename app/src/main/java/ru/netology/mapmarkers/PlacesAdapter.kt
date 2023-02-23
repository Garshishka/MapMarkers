package ru.netology.mapmarkers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.netology.mapmarkers.databinding.PlacesListLayoutBinding

class PlacesAdapter(private val places: List<String>) : RecyclerView.Adapter<PlacesViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val binding =
            PlacesListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlacesViewHolder(binding)
    }

    override fun getItemCount(): Int = places.size

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        val place = places[position]
        holder.bind(place)
    }
}

class PlacesViewHolder(private val binding: PlacesListLayoutBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(point: String) {
        binding.apply {
            placeName.text = "$point"
        }
    }


}