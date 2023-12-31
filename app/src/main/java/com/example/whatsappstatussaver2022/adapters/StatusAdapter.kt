package com.example.whatsappstatussaver2022.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappstatussaver2022.ImagesFragmentDirections
import com.example.whatsappstatussaver2022.R
import com.example.whatsappstatussaver2022.common.sdk29AndUp
import com.example.whatsappstatussaver2022.models.Status
import java.io.File
import kotlin.math.absoluteValue

class StatusAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context: Context
    var statuslist:List<Status> = mutableListOf<Status>()
    fun bindlist(statuslist:List<Status>){
        this.statuslist=statuslist

    }
    inner class ImageViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){

        var image=itemview.findViewById<ImageView>(R.id.image_view)

        fun bind(status: Status){
            sdk29AndUp { Glide.with(context).load(status.fileUri.toUri()).into(image) }?:Glide.with(context).load(File(status.fileUri)).into(image)

        }



    }
    inner class VideoViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){

        var video=itemview.findViewById<ImageView>(R.id.video_view)

        fun bind(status: Status){

            sdk29AndUp { Glide.with(context).load(status.fileUri.toUri()).into(video) }?:Glide.with(context).load(File(status.fileUri)).into(video)

        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context=parent.context

        if(viewType==0){return ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_item,parent,false))}
        else{return VideoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.video_item,parent,false))}


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var status=statuslist[position]
        if(status.isVideo==1){(holder as VideoViewHolder).apply {

            bind(status)
            video.setOnClickListener {
                val action=ImagesFragmentDirections.actionImagesFragmentToImageViewFragment(statuslist.toTypedArray(),position)

                itemView.findNavController().navigate(action)

            }
        }
//            (holder as VideoViewHolder).video.setOnClickListener {
//
//            }



        }
        else{(holder as ImageViewHolder).apply {

            bind(status)
            itemView.setOnClickListener {

                val action=ImagesFragmentDirections.actionImagesFragmentToImageViewFragment(statuslist.toTypedArray(),position)

                itemView.findNavController().navigate(action)


            }
        }}


    }

    override fun getItemCount(): Int {
        return statuslist.size
    }

    override fun getItemViewType(position: Int): Int {
        return statuslist[position].isVideo
    }
}