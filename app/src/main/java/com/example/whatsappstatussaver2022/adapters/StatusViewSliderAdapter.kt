package com.example.whatsappstatussaver2022.adapters

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.system.Os.bind
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappstatussaver2022.ImageViewFragmentDirections
import com.example.whatsappstatussaver2022.R
import com.example.whatsappstatussaver2022.common.savePhotoGalley
import com.example.whatsappstatussaver2022.common.saveVideoGalley
import com.example.whatsappstatussaver2022.common.sdk29AndUp
import com.example.whatsappstatussaver2022.models.Status
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class StatusSliderAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context: Context
    var statuslist:List<Status> = mutableListOf<Status>()
    fun bindlist(statuslist:List<Status>){
        this.statuslist=statuslist

    }
    inner class ImageViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){

        var image=itemview.findViewById<ImageView>(R.id.iio)
        var save=itemview.findViewById<FloatingActionButton>(R.id.save)

var ee=itemview.measuredHeight
        fun bind(status: Status) {
            sdk29AndUp {
                var bm=ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, status.fileUri.toUri()))


image.setImageBitmap(bm)

            }
                ?:Glide.with(context).load(
                    File(status.fileUri)
                ).into(image)
        }



    }
    inner class VideoViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){

        var video=itemview.findViewById<ImageView>(R.id.iio)
   var save=itemview.findViewById<FloatingActionButton>(R.id.save)
        fun bind(status: Status){
            sdk29AndUp { Glide.with(context).load(status.fileUri.toUri()).into(video) } ?: Glide.with(context).load(
                File(status.fileUri)
            ).into(video)
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context=parent.context

        if(viewType==0){return ImageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_slider_item,parent,false))}
        else{return VideoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.video_slider_item,parent,false))}


    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var status=statuslist[position]
        if(status.isVideo==1){(holder as VideoViewHolder).apply {
            bind(status)
            video.setOnClickListener {
                val action=ImageViewFragmentDirections.actionImageViewFragmentToVideoViewFragment(status)
                itemView.findNavController().navigate(action)


          }
            save.setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
                    saveVideoGalley(context,"iio", Uri.parse(status.fileUri))
                }

            }
        }
//            (holder as VideoViewHolder).video.setOnClickListener {
//
//            }



        }
        else{(holder as ImageViewHolder).apply {
            bind(status)
//            image.layoutParams= RecyclerView.LayoutParams(
//                ((image.width)*0.75).toInt(),
//                RecyclerView.LayoutParams.MATCH_PARENT
//            )
////            itemView.setOnClickListener {
//                val action=ImagesFragmentDirections.actionImagesFragmentToImageViewFragment(status)
//
//                itemView.findNavController().navigate(action)
//                Log.d("lll", "pppppppppppppppppppppppppppppppppppppppppppppppppppppppp")
//            }

            save.setOnClickListener {

                var bm=ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, status.fileUri.toUri()))

                CoroutineScope(Dispatchers.IO).launch {
                    savePhotoGalley(context,"tty",bm)
                }

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

fun BITMAP_RESIZER(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
    val scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
    val ratioX = newWidth / bitmap.width.toFloat()
    val ratioY = newHeight / bitmap.height.toFloat()
    val middleX = newWidth / 2.0f
    val middleY = newHeight / 2.0f
    val scaleMatrix = Matrix()
    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
    val canvas = Canvas(scaledBitmap)
    canvas.setMatrix(scaleMatrix)
    canvas.drawBitmap(
        bitmap,
        middleX - bitmap.width / 2,
        middleY - bitmap.height / 2,
        Paint(Paint.FILTER_BITMAP_FLAG)
    )
    return scaledBitmap
}