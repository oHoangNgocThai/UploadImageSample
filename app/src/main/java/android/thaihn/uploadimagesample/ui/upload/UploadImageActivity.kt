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
import android.thaihn.uploadimagesample.base.extension.gone
import android.thaihn.uploadimagesample.base.extension.visible
import android.thaihn.uploadimagesample.entity.UploadResponse
import android.thaihn.uploadimagesample.service.UploadService
import android.thaihn.uploadimagesample.ui.field.FieldSettingActivity
import android.thaihn.uploadimagesample.ui.result.ResultActivity
import android.thaihn.uploadimagesample.ui.setting.SettingActivity
import android.thaihn.uploadimagesample.util.FieldUtil
import android.thaihn.uploadimagesample.util.ImageUtil
import android.thaihn.uploadimagesample.util.UrlUtil
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.gson.Gson
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

        updateUi()

        buttonUpload.setOnClickListener {
            uri?.let {
                if (mFieldSelected.isEmpty()) {
                    Toast.makeText(applicationContext, "Please choose a field", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (mUrlSelected.isEmpty()) {
                    Toast.makeText(applicationContext, "Please choose an url from setting", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                progress.visible()

                uploadImage(it)
            }
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
            R.id.menu_setting -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_setting, menu)
        return true
    }

    override fun onStart() {
        super.onStart()
        updateUi()
    }

    private fun updateUi() {
        getFieldSelected()
        getUrlSelected()

        textContentField.text = mFieldSelected.toString()
    }

    private fun getFieldSelected() {
        mFieldSelected.clear()
        val fields = FieldUtil.getFields()
        fields.forEach {
            if (it.isChecked) {
                mFieldSelected.add(it.title)
            }
        }
    }

    private fun getUrlSelected() {
        val urls = UrlUtil.getUrls()
        urls.forEach {
            if (it.isChecked) {
                mUrlSelected = it.url
            }
        }
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

        val requestBodyField = RequestBody.create(MediaType.parse("text/plain"), Gson().toJson(mFieldSelected))

        try {
            val retrofit = Retrofit.Builder()
                    .baseUrl(mUrlSelected)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            val service = retrofit.create(UploadService::class.java)



            val callUpload: Call<UploadResponse> = service.uploadImage(body, "", requestBodyField)
            callUpload.enqueue(object : Callback<UploadResponse> {
                override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                    t.printStackTrace()
                    progress.gone()
                    Toast.makeText(applicationContext, "Upload fail because ${t.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                        call: Call<UploadResponse>,
                        response: Response<UploadResponse>
                ) {
                    progress.gone()

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
        } catch (ex: java.lang.Exception) {
            progress.gone()
            ex.printStackTrace()
            Toast.makeText(applicationContext, "Error : ${ex.message}", Toast.LENGTH_SHORT).show()
        }

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
