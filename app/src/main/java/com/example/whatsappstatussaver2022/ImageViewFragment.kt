package com.example.whatsappstatussaver2022

import android.graphics.Rect
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.Px
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

class ImageViewFragment : Fragment() {
    private val args by navArgs<ImageViewFragmentArgs>()
lateinit var recyclerView: RecyclerView
lateinit var save:FloatingActionButton
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



    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)?.visibility=View.VISIBLE
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