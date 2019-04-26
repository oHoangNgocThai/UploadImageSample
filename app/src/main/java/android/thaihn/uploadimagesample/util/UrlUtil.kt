package android.thaihn.uploadimagesample.util

import android.thaihn.uploadimagesample.entity.Url
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object UrlUtil {


    fun getUrls(): ArrayList<Url> {
        val urlStr = SharedPrefs.instance[Util.PREF_URLS, String::class.java, ""]
        var urls = arrayListOf<Url>()

        if (urlStr.isNotEmpty()) {
            val type = object : TypeToken<ArrayList<Url>>() {}.type
            urls = Gson().fromJson(urlStr, type)
        }

        return urls
    }

    fun saveUrls(urls: ArrayList<Url>) {
        SharedPrefs.instance.put(Util.PREF_URLS, Gson().toJson(urls))
    }
}
