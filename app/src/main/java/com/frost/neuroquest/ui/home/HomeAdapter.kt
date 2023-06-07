package com.frost.neuroquest.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.frost.neuroquest.R
import com.frost.neuroquest.model.Places
import kotlinx.android.synthetic.main.item.view.*
import java.util.ArrayList

class HomeAdapter : RecyclerView.Adapter<HomeAdapter.MyViewHolder>() {

    private var placesList = ArrayList<Places>()
    private lateinit var context: Context
    var onPlaceClickCallback : ((place: Places) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        return MyViewHolder(inflater)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(placesList[position])
    }

    override fun getItemCount():Int = placesList.size

    inner class MyViewHolder(private val view: View): RecyclerView.ViewHolder(view){

        fun bind(place: Places) {
            view.nameTitle.text = place.nombre
            glideIt(place.image_url)
            view.setOnClickListener { onPlaceClickCallback?.invoke(place) }
        }

        private fun glideIt(url: String) {
            Glide.with(context)
                .load(url)
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view.iv_image)
        }
    }

    fun setList(list: List<Places>, context: Context){
        this.context = context
        placesList = list as ArrayList<Places>
        this.notifyDataSetChanged()
    }
}