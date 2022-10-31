package com.example.whatsappstatussaver2022.adapters

import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.system.Os.bind
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsappstatussaver2022.ImageViewFragmentDirections
import com.example.whatsappstatussaver2022.R
import com.example.whatsappstatussaver2022.common.*

import com.example.whatsappstatussaver2022.models.Status
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


class StatusSliderAdapter(): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var context: Context

    var statuslist:List<Status> = mutableListOf<Status>()
    var savedlist:List<String> = mutableListOf<String>()
    fun bindlist(statuslist:List<Status>){
        this.statuslist=statuslist

    }
    fun savedlist(statuslist:List<String>){
        this.savedlist=statuslist

    }
    inner class ImageViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){



        var image=itemview.findViewById<ImageView>(R.id.iio)
        var save=itemview.findViewById<FloatingActionButton>(R.id.save)
        var share=itemview.findViewById<FloatingActionButton>(R.id.share)
       var delete=itemview.findViewById<FloatingActionButton>(R.id.delete)

        fun bind(status: Status) {
            if(status.filetitle.split(".")[0] in savedlist){save.setImageResource(R.drawable.ic_baseline_delete_24)}

                sdk29AndUp { Glide.with(context).load(status.fileUri.toUri()).into(image) }?:Glide.with(context).load(File(status.fileUri)).into(image)



        }



    }
    inner class VideoViewHolder(itemview: View): RecyclerView.ViewHolder(itemview){

        var video=itemview.findViewById<ImageView>(R.id.iio)
   var save=itemview.findViewById<FloatingActionButton>(R.id.save)
var delete=itemview.findViewById<FloatingActionButton>(R.id.delete)
        var share=itemview.findViewById<FloatingActionButton>(R.id.share)
        fun bind(status: Status){
            if(status.filetitle.split(".")[0] in savedlist){save.setImageResource(R.drawable.ic_baseline_delete_24)}
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


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var status = statuslist[position]

        if (status.isVideo == 1) {
            (holder as VideoViewHolder).apply {
                bind(status)
                video.setOnClickListener {
                    val action =
                        ImageViewFragmentDirections.actionImageViewFragmentToVideoViewFragment(
                            status
                        )
                    itemView.findNavController().navigate(action)


                }

              share.setOnClickListener {
                  shareFile(status,context)
              }
                if(status.filetitle.split(".")[0] in savedlist){
                    save.visibility=View.GONE
                    delete.visibility=View.VISIBLE
                }else{
                    save.visibility=View.VISIBLE
                    delete.visibility=View.GONE
                }
                save.setOnClickListener {

                      var should=true
                var jobg=    CoroutineScope(Dispatchers.IO).launch {
                        context.getSharedPreferences("PHOTOS_IN_GALLERY",Context.MODE_PRIVATE).let { sharedPreferences ->
                            if(sharedPreferences.contains("shouldInGallery")){
                                 should= sharedPreferences.getBoolean("shouldInGallery",true)

                                Log.d("shared", should.toString())
                            }else{
                                sharedPreferences.edit().putBoolean("shouldInGallery",true)
                                Log.d("shared1", should.toString())
                                should=true
                                Log.d("shared2", should.toString())
                            }
                        }
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        jobg.join()
           if(should){
               Log.d("shared", "rrrrrrrrrrrrr")
               launch {
                   saveVideoGallery(context, status.filetitle, Uri.parse(status.fileUri))}
               launch { saveVideoToInternalStorage(status.filetitle,Uri.parse(status.fileUri),context) }

           }else{
               Log.d("shared", "vvvvvvvvvvvvvvvv")
               launch { saveVideoToInternalStorage(status.filetitle,Uri.parse(status.fileUri),context) }}



                    }
                    save.visibility=View.GONE
                    delete.visibility=View.VISIBLE
                }

                delete.setOnClickListener {

                    CoroutineScope(Dispatchers.IO).launch{
                            deletePhotoFromInternalStorage(status.filetitle,context)
                        }
                    var action=ImageViewFragmentDirections.actionImageViewFragmentToImagesFragment()
                        save.findNavController().navigate(action)
                }











            }


        } else {
            (holder as ImageViewHolder).apply {
                bind(status)


           if(status.filetitle.split(".")[0] in savedlist){
               save.visibility=View.GONE
               delete.visibility=View.VISIBLE
           }else{
               save.visibility=View.VISIBLE
               delete.visibility=View.GONE
           }
                share.setOnClickListener {
                    shareFile(status,context)
                }
           save.setOnClickListener {
                 var should=true
               var bm =if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                   ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, status.fileUri.toUri()))
               } else {
                   MediaStore.Images.Media.getBitmap(context.contentResolver, status.fileUri.toUri())
               }
               var jobg=    CoroutineScope(Dispatchers.IO).launch {
                   context.getSharedPreferences("PHOTOS_IN_GALLERY",Context.MODE_PRIVATE).let { sharedPreferences ->
                       if(sharedPreferences.contains("shouldInGallery")){
                           should= sharedPreferences.getBoolean("shouldInGallery",true)

                           Log.d("shared", should.toString())
                       }else{
                           sharedPreferences.edit().putBoolean("shouldInGallery",true)
                           Log.d("shared1", should.toString())
                           should=true
                           Log.d("shared2", should.toString())
                       }
                   }
               }

               CoroutineScope(Dispatchers.IO).launch {
                   jobg.join()
                   if(should){
                       launch{savePhotoGalley(context, status.filetitle, bm)}

                       launch {
                           savePhotoToInternalStorage(status.filetitle, bm, context)
                       }
                   }else{
                       launch {
                           savePhotoToInternalStorage(status.filetitle, bm, context)
                       }
                   }


               }
               save.visibility=View.GONE
               delete.visibility=View.VISIBLE
           }

           delete.setOnClickListener {

    CoroutineScope(Dispatchers.IO).launch{
        deletePhotoFromInternalStorage(status.filetitle,context)
    }
    var action=ImageViewFragmentDirections.actionImageViewFragmentToImagesFragment()
    save.findNavController().navigate(action)
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

