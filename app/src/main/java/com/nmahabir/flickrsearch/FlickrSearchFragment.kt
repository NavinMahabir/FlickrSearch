package com.nmahabir.flickrsearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class FlickrSearchFragment : Fragment() {
    private lateinit var mPhotoRecyclerView: RecyclerView

    companion object {
        fun newInstance(): FlickrSearchFragment {
            return FlickrSearchFragment()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_photo_gallery, container, false)

        mPhotoRecyclerView = v.findViewById(R.id.photo_recycler_view)
        mPhotoRecyclerView.setLayoutManager(GridLayoutManager(activity, 3))

        return v

    }

}