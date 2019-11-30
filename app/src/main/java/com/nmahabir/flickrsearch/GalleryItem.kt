package com.nmahabir.flickrsearch

class GalleryItem(_caption:String, _id:String, _url:String) {
    var caption: String
    var id: String
    var url: String

    init {
        this.caption = _caption
        this.id = _id
        this.url = _url
    }

    override fun toString(): String {
        return caption
    }
}
