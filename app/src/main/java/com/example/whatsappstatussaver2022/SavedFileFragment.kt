package com.example.whatsappstatussaver2022

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappstatussaver2022.adapters.SavedFileAdapter
import com.example.whatsappstatussaver2022.common.loadFilesFromInternalStorage
import kotlinx.coroutines.*


class SavedFileFragment : Fragment() {
  lateinit var recyclerView: RecyclerView
   lateinit var oop:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        var view= inflater.inflate(R.layout.fragment_saved_file, container, false)
         recyclerView=view.findViewById(R.id.savedfilesrecyclerview)
       oop=view.findViewById(R.id.imageView)

         CoroutineScope(Dispatchers.IO).launch {
           var statuses= loadFilesFromInternalStorage(requireContext())



     withContext(Dispatchers.Main){
         // oop.setImageURI(photos[0].contentUri)
         recyclerView.layoutManager=GridLayoutManager(requireContext(),3)
         var photoadapter=SavedFileAdapter()
         recyclerView.adapter=photoadapter
          photoadapter.bindlist(statuses)}



         }

        return view
    }

}