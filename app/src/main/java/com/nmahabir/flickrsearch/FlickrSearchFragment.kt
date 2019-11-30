package com.nmahabir.flickrsearch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.AsyncTask
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide


class FlickrSearchFragment : Fragment() {
    private lateinit var mPhotoRecyclerView: RecyclerView
    private var mItems: List<GalleryItem> = ArrayList()

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

        setupAdapter()

        FetchItemsTask("dogs").execute()

        return v

    }

    private fun setupAdapter() {
        if (isAdded()) {
            mPhotoRecyclerView.adapter = PhotoAdapter((mItems))
        }
    }

    private inner class PhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val mItemImageView: ImageView = itemView.findViewById(R.id.item_image_view) as ImageView

        fun bindDrawable(drawable: Drawable) {
            mItemImageView.setImageDrawable(drawable)
        }
    }


    private inner class PhotoAdapter(private val mGalleryItems: List<GalleryItem>) :
        RecyclerView.Adapter<PhotoHolder>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PhotoHolder {
            val inflater = LayoutInflater.from(activity)
            val view = inflater.inflate(R.layout.list_item_gallery, viewGroup, false)

            return PhotoHolder(view)
        }

        override fun onBindViewHolder(photoHolder: PhotoHolder, position: Int) {
            val galleryItem = mGalleryItems[position]
            val placeholder = ContextCompat.getDrawable(view?.context!!, R.drawable.loading_spinner)!!
            photoHolder.bindDrawable(placeholder)

            Glide.with(view!!)
                .load(galleryItem.url)
                .centerCrop()
                .placeholder(R.drawable.loading_spinner)
                .error(R.drawable.error)
                .into(photoHolder.mItemImageView)
        }

        override fun getItemCount(): Int {
            return mGalleryItems.size
        }
    }


    private inner class FetchItemsTask(private val mQuery: String?) : AsyncTask<Void, Void, List<GalleryItem>>() {

        override fun doInBackground(vararg params: Void): List<GalleryItem> {
            return if (mQuery == null) {
                FlickrFetchr().fetchRecentPhotos()
            } else {
                FlickrFetchr().searchPhotos(mQuery)
            }
        }

        override fun onPostExecute(items: List<GalleryItem>) {
            mItems = items
            setupAdapter()
        }
    }
}