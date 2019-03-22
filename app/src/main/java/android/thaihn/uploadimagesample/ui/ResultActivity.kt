package android.thaihn.uploadimagesample.ui

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.thaihn.uploadimagesample.R
import android.thaihn.uploadimagesample.databinding.ActivityResultBinding
import android.thaihn.uploadimagesample.entity.UploadResponse
import android.view.MenuItem

class ResultActivity : AppCompatActivity() {

    companion object {
        private val TAG = ResultActivity::class.java.simpleName

        private const val UPLOAD_RESPONSE = "upload_response"

        fun startActivity(context: Context, response: UploadResponse) {
            val intent = Intent(context, ResultActivity::class.java).apply {
                putExtra(UPLOAD_RESPONSE, response)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    private var uploadResponse: UploadResponse? = null

    private lateinit var resultBinding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultBinding = DataBindingUtil.setContentView(this, R.layout.activity_result)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        uploadResponse = intent.getParcelableExtra(UPLOAD_RESPONSE)

        resultBinding.tvData1Result.text = uploadResponse?.data?.data1.toString()
        resultBinding.tvData2Result.text = uploadResponse?.data?.data2.toString()
        resultBinding.tvFormName.text = uploadResponse?.data?.form_name
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
