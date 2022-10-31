package com.example.whatsappstatussaver2022

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Px
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappstatussaver2022.adapters.StatusSliderAdapter
import com.example.whatsappstatussaver2022.common.loadallFilesFromInternalStorage
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.ArrayList

class ImageViewFragment : Fragment() {
    private val args by navArgs<ImageViewFragmentArgs>()
lateinit var recyclerView: RecyclerView
lateinit var save:FloatingActionButton
    var isReadPermissionGranted:Boolean=false
    var isWritePermissionGranted:Boolean=false
    lateinit var permissionlauncher: ActivityResultLauncher<Array<String>>
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        var view= inflater.inflate(R.layout.fragment_image_view, container, false)










        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    Toast.makeText(context,"SWIPE TO SEE NEXT",Toast.LENGTH_SHORT).show()
        permissionlauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                permissions->
            //   isManageExternalstoragePermissionGranted=permissions[Manifest.permission.MANAGE_EXTERNAL_STORAGE] ?: isManageExternalstoragePermissionGranted
            isReadPermissionGranted=permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: isReadPermissionGranted
            isWritePermissionGranted=permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionGranted
           if(!isReadPermissionGranted){requestOrUpdatePermissions()}
            if(!isWritePermissionGranted){requestOrUpdatePermissions()}

        }
        requestOrUpdatePermissions()
        recyclerView=view.findViewById(R.id.sliderrecyclerview)

        var layoutmanager=LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
        recyclerView.layoutManager=layoutmanager

        PagerSnapHelper().attachToRecyclerView(recyclerView)
        recyclerView.addItemDecoration(LinearHorizontalSpacingDecoration(80))
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility=View.GONE





        var adapter=StatusSliderAdapter()

        recyclerView.adapter=adapter

        adapter.bindlist(args.statuslist.toMutableList())

        recyclerView.scrollToPosition(args.position)
CoroutineScope(Dispatchers.IO).launch {
    var list= loadallFilesFromInternalStorage(requireContext())
    adapter.savedlist(list)
}


    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility=View.VISIBLE
    }
    private fun requestOrUpdatePermissions(){

        isReadPermissionGranted= ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED


        isWritePermissionGranted= ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED


//        isManageExternalstoragePermissionGranted= ContextCompat.checkSelfPermission(this,Manifest.permission.MANAGE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
//        Log.d("anmol", isManageExternalstoragePermissionGranted.toString())
        val permissionrequest:MutableList<String> = ArrayList()


        if(!isReadPermissionGranted) {
            permissionrequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if(!isWritePermissionGranted) {
            permissionrequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }


        if(permissionrequest.isNotEmpty()){
            permissionlauncher.launch(permissionrequest.toTypedArray())

        }

    }

}

class LinearHorizontalSpacingDecoration(@Px private val innerSpacing: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemPosition = parent.getChildAdapterPosition(view)

        outRect.left =  innerSpacing/2
        outRect.right =  innerSpacing/2
        outRect.top=30
        outRect.bottom=30
    }
}