package android.thaihn.uploadimagesample.ui

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.thaihn.uploadimagesample.databinding.ActivityUploadImageBinding
import android.thaihn.uploadimagesample.entity.UploadResponse
import android.thaihn.uploadimagesample.service.UploadService
import android.thaihn.uploadimagesample.util.ImageUtil
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor




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
        uploadImageBinding = DataBindingUtil.setContentView(this, android.thaihn.uploadimagesample.R.layout.activity_upload_image)

        supportActionBar?.title = "Preview Image"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        uri = intent?.getStringExtra(FILE_URI)

        Log.d(TAG, "File uri $uri")

        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(uri))
        uploadImageBinding.imgPreview.setImageBitmap(bitmap)

        uploadImageBinding.btnUpload.setOnClickListener {
            uri?.let {
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

        val requestFile = RequestBody.create(MediaType.parse(contentResolver.getType(realUri)), file)
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
                Toast.makeText(applicationContext, "Upload fail because ${t.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<UploadResponse>, response: Response<UploadResponse>) {
                Log.d(TAG, "onResponse: ${response.body()}")
                response.body()?.let {
                    Toast.makeText(applicationContext, "Upload success", Toast.LENGTH_SHORT).show()
                    ResultActivity.startActivity(applicationContext, it)
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

}
