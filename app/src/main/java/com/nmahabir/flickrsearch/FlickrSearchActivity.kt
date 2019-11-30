package com.nmahabir.flickrsearch


class FlickrSearchActivity : SingleFragmentActivity() {

    override fun createFragment(): FlickrSearchFragment {
        return FlickrSearchFragment.newInstance()
    }
}
