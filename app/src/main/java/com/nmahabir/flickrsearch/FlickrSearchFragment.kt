package com.nmahabir.flickrsearch

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.os.AsyncTask
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.*
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide


class FlickrSearchFragment : Fragment() {
    private val TAG = "FlickrSearchFragment"
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
        setHasOptionsMenu(true)
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
        updateItems()

        return v

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.fragment_flickr_search, menu)

        val searchItem = menu?.findItem(R.id.menu_item_search)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                Log.d(TAG, "QueryTextSubmit: $s")
                QueryPreferences.setStoredQuery(activity!!, s)
                updateItems()
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                Log.d(TAG, "QueryTextChange: $s")
                return false
            }
        })

        searchView.setOnSearchClickListener(View.OnClickListener {
            val query = QueryPreferences.getStoredQuery(activity!!)
            searchView.setQuery(query, false)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.menu_item_clear -> {
                QueryPreferences.setStoredQuery(activity!!, "")
                updateItems()
                true
            }
            else -> super.onOptionsItemSelected(item)

        }
    }

    private fun updateItems() {
        val query = QueryPreferences.getStoredQuery(activity!!)
        FetchItemsTask(if (query != "") query else null).execute()
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