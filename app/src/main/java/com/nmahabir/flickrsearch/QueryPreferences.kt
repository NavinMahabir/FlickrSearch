package com.nmahabir.flickrsearch

import android.content.Context


class QueryPreferences {

    companion object {
        private var PREF_SEARCH_QUERY = "searchQuery"

        fun getStoredQuery(context: Context): String {
            return context.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)
                .getString(PREF_SEARCH_QUERY, "") ?: ""
        }

        fun setStoredQuery(context: Context, query: String) {
            val mPrefs = context.getSharedPreferences("myAppPrefs", Context.MODE_PRIVATE)
            val editor = mPrefs.edit()
            editor.putString(PREF_SEARCH_QUERY, query)
            editor.apply()
        }
    }


}