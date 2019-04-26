package android.thaihn.uploadimagesample.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.thaihn.uploadimagesample.R
import android.thaihn.uploadimagesample.util.SharedPrefs
import android.thaihn.uploadimagesample.util.Util
import android.view.MenuItem
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_add_url.*

class AddUrlActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_url)

        supportActionBar?.title = "Add New Url"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        buttonSave.setOnClickListener {
            saveUrl()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun saveUrl() {
//        val url = editTextUrl.text.toString().trim()
//
//        if (url.isEmpty()) {
//            return
//        }
//
////        val listUrlSrt = SharedPrefs.instance[Util.PREF_LIST_URLS, String::class.java, ""]
//        if (listUrlSrt.isNotEmpty()) {
//            val type = object : TypeToken<ArrayList<String>>() {}.type
//            val urls: ArrayList<String> = Gson().fromJson(listUrlSrt, type)
//
//            var existed = false
//            urls.forEach {
//                if (url == it) {
//                    existed = true
//                    return@forEach
//                }
//            }
//
//            if (existed) {
//                Toast.makeText(applicationContext, "Url already exist", Toast.LENGTH_SHORT).show()
//            } else {
//                urls.add(0, url)
//                SharedPrefs.instance.put(Util.PREF_LIST_URLS, Gson().toJson(urls))
//                Toast.makeText(applicationContext, "Save url success", Toast.LENGTH_SHORT).show()
//                clearInput()
//            }
//        }
    }

    private fun clearInput() {
        editTextUrl.text.clear()
    }
}
