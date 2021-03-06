package android.thaihn.uploadimagesample.ui.result

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

        private const val UPLOAD_ANY = "upload_any"

        fun startActivityWithAny(context: Context, data: String) {
            val intent = Intent(context, ResultActivity::class.java).apply {
                putExtra(UPLOAD_ANY, data)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }

    private var uploadString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        uploadString = intent?.getStringExtra(UPLOAD_ANY)

        uploadString?.let {
            jsonLayout.bindJson(it)
            jsonLayout.expandAll()
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
}
