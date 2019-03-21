package android.thaihn.uploadimagesample.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore.Images
import android.support.v7.app.AppCompatActivity
import android.thaihn.uploadimagesample.R
import android.thaihn.uploadimagesample.databinding.ActivityMainBinding
import android.widget.Toast
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        private const val REQUEST_CODE_CAMERA_OPENED = 1
        private const val REQUEST_CODE_LIBRARY_OPENED = 2
        private const val REQUEST_PERMISSION_CAMERA = 3
        private const val REQUEST_PERMISSION_LIBRARY = 4
    }

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
                    val bitmapPhoto = data?.extras?.get("data") as Bitmap
                    val uri = getImageUri(bitmapPhoto)
                    UploadImageActivity.startActivity(this, uri.toString())
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
            REQUEST_PERMISSION_LIBRARY -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openLibrary()
                } else {
                    Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, REQUEST_CODE_CAMERA_OPENED)
    }

    private fun openLibrary() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
        }
        val pickIntent =
            Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ).apply {
                type = "image/*"
            }
        val chooseIntent = Intent.createChooser(intent, "Select Image").apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(pickIntent))
        }
        startActivityForResult(chooseIntent, REQUEST_CODE_LIBRARY_OPENED)
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
                    REQUEST_PERMISSION_LIBRARY
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
                    REQUEST_PERMISSION_LIBRARY
                )
            }
        }
    }

    private fun getImageUri(bm: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = Images.Media.insertImage(applicationContext.contentResolver, bm, "Title", null)
        return Uri.parse(path)
    }
}
