package android.thaihn.uploadimagesample.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.thaihn.uploadimagesample.R
import android.thaihn.uploadimagesample.base.BaseActivity
import android.thaihn.uploadimagesample.entity.Field
import android.thaihn.uploadimagesample.entity.Url
import android.thaihn.uploadimagesample.ui.setting.SettingActivity
import android.thaihn.uploadimagesample.ui.upload.UploadImageActivity
import android.thaihn.uploadimagesample.util.FieldUtil
import android.thaihn.uploadimagesample.util.UrlUtil
import android.thaihn.uploadimagesample.util.Util
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : BaseActivity() {

    companion object {
        private val TAG = MainActivity::class.java.simpleName

        private const val REQUEST_CODE_CAMERA_OPENED = 1
        private const val REQUEST_CODE_LIBRARY_OPENED = 2
        private const val REQUEST_PERMISSION_CAMERA = 3
        private const val REQUEST_PERMISSION_LIBRARY_READ = 4
        private const val REQUEST_PERMISSION_LIBRARY_WRITE = 5
    }

    private var currentPhotoPath: String? = null

    override val layoutResource: Int
        get() = R.layout.activity_main

    override fun initComponent(savedInstanceState: Bundle?) {
        supportActionBar?.title = "OCR APP"

        checkPermission()
        setupDefaultValue()

        buttonCamera.setOnClickListener {
            checkPermissionCamera()
        }

        buttonLibrary.setOnClickListener {
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
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
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
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

    private fun setupDefaultValue() {
        val mFields = FieldUtil.getFields()
        if (mFields.isEmpty()) {
            mFields.add(Field(Util.DEFAULT_FIELD_1, true))
            mFields.add(Field(Util.DEFAULT_FIELD_2, false))
            FieldUtil.saveFields(mFields)
        }

        val mUrls = UrlUtil.getUrls()
        if (mUrls.isEmpty()) {
            mUrls.add(Url(Util.DEFAULT_URL, true))
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
