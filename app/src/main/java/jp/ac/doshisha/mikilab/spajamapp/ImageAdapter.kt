package jp.ac.doshisha.mikilab.spajamapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class ImageAdapter(
    private val imageList: List<Image>,
    private val imageID: Int,
    private val containerID: Int
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    class ViewHolder(view: View, imageID: Int) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(imageID)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(containerID, parent, false)
        return ViewHolder(view, imageID)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val image = imageList[position]
        holder.image.setImageResource(image.imageID)
    }

    override fun getItemCount() = imageList.size
}