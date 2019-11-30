package com.nmahabir.flickrsearch

class GalleryItem(_caption:String, _id:String, _url:String) {
    var caption: String = _caption
    var id: String = _id
    var url: String = _url

    override fun toString(): String {
        return caption
    }
}
