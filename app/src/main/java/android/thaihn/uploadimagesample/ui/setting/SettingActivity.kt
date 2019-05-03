package android.thaihn.uploadimagesample.ui.setting

import android.content.Intent
import android.os.Bundle
import android.thaihn.uploadimagesample.R
import android.thaihn.uploadimagesample.base.BaseActivity
import android.thaihn.uploadimagesample.ui.field.FieldSettingActivity
import android.thaihn.uploadimagesample.ui.url.UrlSettingActivity
import android.thaihn.uploadimagesample.util.FieldUtil
import android.thaihn.uploadimagesample.util.UrlUtil
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_setting.*

class SettingActivity : BaseActivity() {

    override val layoutResource: Int
        get() = R.layout.activity_setting

    private var mFieldSelected = arrayListOf<String>()

    private var mUrlSelected = ""

    override fun initComponent(savedInstanceState: Bundle?) {
        supportActionBar?.title = "Settings"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        imageEditUrl.setOnClickListener {
            startActivity(Intent(this, UrlSettingActivity::class.java))
        }

        imageEditField.setOnClickListener {
            startActivity(Intent(this, FieldSettingActivity::class.java))
        }

        updateUi()
    }

    override fun onStart() {
        super.onStart()
        updateUi()
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

    private fun updateUi() {
        getFieldSelected()
        getUrlSelected()

        textContentField.text = mFieldSelected.toString()
        textContentUrl.text = mUrlSelected
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
}
