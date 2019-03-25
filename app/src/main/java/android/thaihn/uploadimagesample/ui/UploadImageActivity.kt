package android.thaihn.uploadimagesample.ui

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.thaihn.uploadimagesample.R
import android.thaihn.uploadimagesample.databinding.ActivityUploadImageBinding
import android.thaihn.uploadimagesample.entity.UploadResponse
import android.thaihn.uploadimagesample.service.UploadService
import android.thaihn.uploadimagesample.util.ImageUtil
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
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


class UploadImageActivity : AppCompatActivity() {

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

    private lateinit var uploadImageBinding: ActivityUploadImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        uploadImageBinding = DataBindingUtil.setContentView(this, R.layout.activity_upload_image)

        supportActionBar?.title = "Preview Image"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        uri = intent?.getStringExtra(FILE_URI)

        Log.d(TAG, "File uri $uri")
        val path = ImageUtil.getPathFromUri(applicationContext, Uri.parse(uri))

        path?.let {
            val bitmap = getBitmap(it)
            uploadImageBinding.imgPreview.setImageBitmap(bitmap)
        }

        uploadImageBinding.btnUpload.setOnClickListener {
            uri?.let {
                uploadImageBinding.progress.visibility = View.VISIBLE
                uploadImage(it)
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

        val BASE_URL = "http://192.168.19.18:9669"
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(UploadService::class.java)

        val callUpload: Call<UploadResponse> = service.uploadImage(body, "")
        callUpload.enqueue(object : Callback<UploadResponse> {
            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
                t.printStackTrace()
                uploadImageBinding.progress.visibility = View.GONE
                Toast.makeText(applicationContext, "Upload fail because ${t.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {
                uploadImageBinding.progress.visibility = View.GONE

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
                        val jsonObject = JSONObject(it)
                        val code = jsonObject.optString("code")
                        val message = jsonObject.optString("message")
                        Log.d(TAG, "ErrorResponse: code:$code---message:$message")
                        Toast.makeText(applicationContext,"Code: $code -- Message: $message", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
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
