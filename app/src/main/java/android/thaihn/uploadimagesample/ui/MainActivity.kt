package android.thaihn.uploadimagesample.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.thaihn.uploadimagesample.R
import android.thaihn.uploadimagesample.databinding.ActivityMainBinding
import android.util.Log
import android.widget.Toast
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        private const val REQUEST_CODE_CAMERA_OPENED = 1
        private const val REQUEST_CODE_LIBRARY_OPENED = 2
        private const val REQUEST_PERMISSION_CAMERA = 3
        private const val REQUEST_PERMISSION_LIBRARY_READ = 4
        private const val REQUEST_PERMISSION_LIBRARY_WRITE = 5
    }

    var currentPhotoPath: String? = null

    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        actionBar?.title = "OCR APP"

        checkPermission()

        mainBinding.btnCamera.setOnClickListener {
            checkPermissionCamera()
        }

        mainBinding.btnLibrary.setOnClickListener {
            checkPermissionLibrary()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CODE_CAMERA_OPENED -> {
                if (resultCode == Activity.RESULT_OK) {
                    galleryAddPic()
                    currentPhotoPath?.let {
                        val file = File(it)
                        val uri = Uri.fromFile(file)
                        UploadImageActivity.startActivity(this, uri.toString())
                    }
                }
            }
            REQUEST_CODE_LIBRARY_OPENED -> {
                if (resultCode == Activity.RESULT_OK) {
                    data?.data?.let {
                        UploadImageActivity.startActivity(this, it.toString())
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSION_CAMERA -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            REQUEST_PERMISSION_LIBRARY_READ -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openLibrary()
                } else {
                    Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            REQUEST_PERMISSION_LIBRARY_WRITE -> {
            }
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {

                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }

                // Continue only if the File was successfully created
                photoFile?.also {
                    val authority = applicationContext.packageName + ".fileprovider"
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        authority,
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_CODE_CAMERA_OPENED)
                }

            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    private fun galleryAddPic() {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent->
            currentPhotoPath?.let {
                val f = File(it)
                mediaScanIntent.data = Uri.fromFile(f)
                sendBroadcast(mediaScanIntent)
            }
        }
    }

    private fun openLibrary() {
        Intent(Intent.ACTION_GET_CONTENT).also { intentChoose ->
            intentChoose.type = "image/*"
            intentChoose.resolveActivity(packageManager)?.also {
                startActivityForResult(intentChoose, REQUEST_CODE_LIBRARY_OPENED)
            }
        }

    }

    private fun checkPermissionCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_PERMISSION_CAMERA)
            } else {
                openCamera()
            }
        } else {
            openCamera()
        }
    }

    private fun checkPermissionLibrary() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_CALENDAR),
                    REQUEST_PERMISSION_LIBRARY_READ
                )
            } else {
                openLibrary()
            }
        } else {
            openLibrary()
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_PERMISSION_LIBRARY_WRITE
                )
            }
        }
    }
}
