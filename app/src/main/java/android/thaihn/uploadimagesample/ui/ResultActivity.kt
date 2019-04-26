package android.thaihn.uploadimagesample.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.thaihn.uploadimagesample.R
import android.thaihn.uploadimagesample.entity.UploadResponse
import android.view.MenuItem
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_result.*

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        uploadResponse = intent.getParcelableExtra(UPLOAD_RESPONSE)

        textResult.text = Gson().toJson(uploadResponse)
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
