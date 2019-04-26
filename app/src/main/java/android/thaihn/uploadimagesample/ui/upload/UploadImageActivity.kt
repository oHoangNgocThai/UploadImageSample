package android.thaihn.uploadimagesample.ui.upload

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.thaihn.uploadimagesample.R
import android.thaihn.uploadimagesample.base.BaseActivity
import android.thaihn.uploadimagesample.entity.UploadResponse
import android.thaihn.uploadimagesample.service.UploadService
import android.thaihn.uploadimagesample.ui.ResultActivity
import android.thaihn.uploadimagesample.ui.UrlSettingActivity
import android.thaihn.uploadimagesample.ui.field.FieldSettingActivity
import android.thaihn.uploadimagesample.util.FieldUtil
import android.thaihn.uploadimagesample.util.ImageUtil
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_upload_image.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class UploadImageActivity : BaseActivity() {

    companion object {

        private val TAG = UploadImageActivity::class.java.simpleName

        private const val FILE_URI = "file_uri"

        fun startActivity(context: Context, uri: String) {
            val intent = Intent(context, UploadImageActivity::class.java).apply {
                putExtra(FILE_URI, uri)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    private var uri: String? = null

    private var mFieldSelected = arrayListOf<String>()

    private var mUrls = arrayListOf<String>()
    private var mUrlSelected = ""


    override val layoutResource: Int
        get() = R.layout.activity_upload_image

    override fun initComponent(savedInstanceState: Bundle?) {
        supportActionBar?.title = "Preview Image"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        uri = intent?.getStringExtra(FILE_URI)

        // get Path of Image
        Log.d(TAG, "File uri $uri")
        ImageUtil.getPathFromUri(applicationContext, Uri.parse(uri))?.let {
            val bitmap = getBitmap(it)
            imagePreview.setImageBitmap(bitmap)
        }

        // get Fields
        initFields()

        updateUi()

        buttonUpload.setOnClickListener {
            uri?.let {
                progress.visibility = View.VISIBLE
                uploadImage(it)
            }
        }

        imageEditUrl.setOnClickListener {
            startActivity(Intent(this, UrlSettingActivity::class.java))
        }

        imageEditField.setOnClickListener {
            startActivity(Intent(this, FieldSettingActivity::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        initFields()
        updateUi()
    }

    private fun updateUi() {
        textContentField.text = mFieldSelected.toString()

    }

    private fun initFields() {
        if (FieldUtil.checkFieldExist()) {
            mFieldSelected.clear()
            val fields = FieldUtil.getFields()
            fields.forEach {
                if (it.isChecked) {
                    mFieldSelected.add(it.title)
                }
            }
        }
    }

//    private fun keyToValueFields(position: Int): ArrayList<String> {
//        val result = arrayListOf<String>()
//        when (fields[position]) {
//            FieldType.ALL.key -> {
//                result.add(FieldType.ALL.value)
//            }
//            FieldType.TWO_REGIONS_OF_MONTH.key -> {
//                result.add(FieldType.TWO_REGIONS_OF_MONTH.value)
//            }
//            FieldType.TWO_REGIONS_OF_MONEY.key -> {
//                result.add(FieldType.TWO_REGIONS_OF_MONEY.value)
//            }
//            FieldType.FOUR_REGIONS_OF_BOTH.key -> {
//                result.add(FieldType.TWO_REGIONS_OF_MONEY.value)
//                result.add(FieldType.TWO_REGIONS_OF_MONTH.value)
//            }
//        }
//        return result
//    }

//    private fun initFields() {
//        val fieldAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fields)
//        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerFields.adapter = fieldAdapter
//        spinnerFields.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                mFieldSelected = keyToValueFields(position)
//                Log.d(TAG, "Field Selected: $mFieldSelected")
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                Log.d(TAG, "Nothing selected")
//            }
//        }
//    }

    private fun initBaseUrl() {
//        val urlsAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, mUrls)
//        urlsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        spinnerBaseUrl.adapter = urlsAdapter
//        spinnerBaseUrl.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                mUrlSelected = mUrls[position]
//                mUrls.removeAt(position)
//                mUrls.add(0, mUrlSelected)
//                SharedPrefs.instance.put(Util.PREF_LIST_URLS, Gson().toJson(mUrls))
//                Log.d(TAG, "Url Selected: $mUrlSelected")
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                Log.d(TAG, "Nothing selected")
//            }
//        }
    }


//    private fun getUrls(): ArrayList<String> {
//        val listUrlSrt = SharedPrefs.instance[Util.PREF_LIST_URLS, String::class.java, ""]
//        var urls = arrayListOf<String>()
//        if (listUrlSrt.isNotEmpty()) {
//            val type = object : TypeToken<ArrayList<String>>() {}.type
//
//            urls = Gson().fromJson(listUrlSrt, type)
//        }
//        return urls
//    }

    private fun initDataUrl() {
//        val listUrlSrt = SharedPrefs.instance[Util.PREF_LIST_URLS, String::class.java, ""]
//        val urls = arrayListOf<String>()
//        if (listUrlSrt.isEmpty()) {
//            // save default url
//            urls.add(Util.DEFAULT_URL)
//            SharedPrefs.instance.put(Util.PREF_LIST_URLS, Gson().toJson(urls))
//        }
    }

    private fun uploadImage(uri: String) {
        val realUri = Uri.parse(uri)
        val path = ImageUtil.getPathFromUri(applicationContext, realUri)
        val file = File(path)

        // Log
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val requestFile =
                RequestBody.create(MediaType.parse("image/*"), file)
        val body = MultipartBody.Part.createFormData("image", file.name, requestFile)

        val retrofit = Retrofit.Builder()
                .baseUrl(mUrlSelected)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val service = retrofit.create(UploadService::class.java)

        val callUpload: Call<UploadResponse> = service.uploadImage(body, "", mFieldSelected)
        callUpload.enqueue(object : Callback<UploadResponse> {
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                t.printStackTrace()
                progress.visibility = View.GONE
                Toast.makeText(applicationContext, "Upload fail because ${t.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                    call: Call<UploadResponse>,
                    response: Response<UploadResponse>
            ) {
                progress.visibility = View.GONE

                val code = response.code()
                Log.d(TAG, "Code: $code")
                if (code == 200) {
                    response.body()?.let {
                        Log.d(TAG, "body: $it")
                        Toast.makeText(applicationContext, "Upload success", Toast.LENGTH_SHORT).show()
                        ResultActivity.startActivity(applicationContext, it)
                    }
                } else {
                    response.errorBody()?.string()?.let {
                        Log.d(TAG, "errorBody: $it")
                        try {
                            val jsonObject = JSONObject(it)
                            val code = jsonObject.optString("code")
                            val message = jsonObject.optString("message")
                            Log.d(TAG, "ErrorResponse: code:$code---message:$message")
                            Toast.makeText(applicationContext, "Code: $code -- Message: $message", Toast.LENGTH_SHORT).show()
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                            Log.d(TAG, "Error parser response: ${ex.message}")
                            Toast.makeText(applicationContext, "Error parser response: ${ex.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        })
    }

    private fun getBitmap(path: String): Bitmap {
        var bitmap = BitmapFactory.decodeFile(path)

        val exif = ExifInterface(path)
        val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1)

        var rotation: Float = 0F
        when (orientation) {
            6 -> rotation = 90F
            3 -> rotation = 180F
            8 -> rotation = 270F
        }

        if (rotation != 0F) {
            val matrix = Matrix()
            matrix.postRotate(rotation)

            var rotated =
                    Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            bitmap.recycle()
            bitmap = rotated
            rotated = null
        }

        return bitmap
    }
}
