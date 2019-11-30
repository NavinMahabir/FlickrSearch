package com.nmahabir.flickrsearch

import android.net.Uri
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONException
import org.json.JSONObject


class FlickrFetchr {

    private val TAG = "FlickrFetchr"

    private val API_KEY = "675894853ae8ec6c242fa4c077bcf4a0"
    private val FETCH_RECENTS_METHOD = "flickr.photos.getRecent"
    private val SEARCH_METHOD = "flickr.photos.search"
    private val ENDPOINT = Uri
        .parse("https://api.flickr.com/services/rest/")
        .buildUpon()
        .appendQueryParameter("api_key", API_KEY)
        .appendQueryParameter("format", "json")
        .appendQueryParameter("nojsoncallback", "1")
        .appendQueryParameter("extras", "url_s")
        .build()

    @Throws(IOException::class)
    fun getUrlString(urlSpec:String):String {
        val url = URL(urlSpec)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        try {
            return connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
//        try {
//            val out = ByteArrayOutputStream()
//            val input = connection.getInputStream()
//
//            if (connection.getResponseCode() !== HttpURLConnection.HTTP_OK) {
//                throw IOException(
//                    connection.getResponseMessage() +
//                            ": with " +
//                            urlSpec
//                )
//            }
//
//            var bytesRead = 0
//            val buffer = ByteArray(1024)
//            bytesRead = input.read(buffer)
//            while (bytesRead > 0) {
//                out.write(buffer, 0, bytesRead)
//                bytesRead = input.read(buffer)
//            }
//            out.close()
//            return out.toByteArray()
//        } finally {
//            connection.disconnect()
//        }
    }

//    @Throws(IOException::class)
//    fun getUrlString(urlSpec: String): String {
//        return getUrlBytes(urlSpec).toString()
//    }

    fun fetchRecentPhotos(): List<GalleryItem> {
        val url = buildUrl(FETCH_RECENTS_METHOD, null)
        return downloadGalleryItems(url)
    }

    fun searchPhotos(query: String): List<GalleryItem> {
        val url = buildUrl(SEARCH_METHOD, query)
        return downloadGalleryItems(url)
    }

    fun downloadGalleryItems(url: String): List<GalleryItem> {
        val items = ArrayList<GalleryItem>()

        try {
            val jsonString = getUrlString(url)
            Log.i(TAG, "Received JSON: $jsonString")
            val jsonBody = JSONObject(jsonString)
            parseItems(items, jsonBody)
        } catch (ioe: IOException) {
            Log.e(TAG, "Failed to fetch items", ioe)
        } catch (je: JSONException) {
            Log.e(TAG, "Failed to parse JSON", je)
        }

        return items
    }

    private fun buildUrl(method: String, query: String?): String {
        val uriBuilder = ENDPOINT.buildUpon()
            .appendQueryParameter("method", method)

        if (method == SEARCH_METHOD) {
            uriBuilder.appendQueryParameter("text", query)
        }

        return uriBuilder.build().toString()
    }

    @Throws(IOException::class, JSONException::class)
    private fun parseItems(items: MutableList<GalleryItem>, jsonBody: JSONObject) {

        val photosJsonObject = jsonBody.getJSONObject("photos")
        val photoJsonArray = photosJsonObject.getJSONArray("photo")

        for (i in 0 until photoJsonArray.length()) {
            val photoJsonObject = photoJsonArray.getJSONObject(i)

            val id = photoJsonObject.getString("id")
            val caption = photoJsonObject.getString("title")

            if (!photoJsonObject.has("url_s")) {
                continue
            }

            val url = photoJsonObject.getString("url_s")

            val item = GalleryItem(caption, id, url)
            items.add(item)
        }
    }

}