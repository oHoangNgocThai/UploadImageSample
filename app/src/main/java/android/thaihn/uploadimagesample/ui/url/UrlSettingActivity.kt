package android.thaihn.uploadimagesample.ui.url

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.thaihn.uploadimagesample.R
import android.thaihn.uploadimagesample.base.BaseActivity
import android.thaihn.uploadimagesample.entity.Url
import android.thaihn.uploadimagesample.util.UrlUtil
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_url_setting.*

class UrlSettingActivity : BaseActivity(), UrlAdapter.UrlListener {

    override val layoutResource: Int
        get() = R.layout.activity_url_setting

    private val mUrlAdapter = UrlAdapter(this)

    private var mUrls = arrayListOf<Url>()
    private var mUrlSelected = Pair(-1, "")

    override fun initComponent(savedInstanceState: Bundle?) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycleUrl.apply {
            adapter = mUrlAdapter
            layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        }

        mUrls = UrlUtil.getUrls()
        updateUi()

        buttonAdd.setOnClickListener {
            val url = editTextUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                mUrls.add(Url(url, false))
                editTextUrl.text.clear()
                updateUi()
            } else {
                Toast.makeText(applicationContext, "Url is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.menu_save -> {
                UrlUtil.saveUrls(mUrls)
                finish()
            }
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        UrlUtil.saveUrls(mUrls)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onClickListener(position: Int, item: Url) {
        if (mUrlSelected.first != -1) {
            val oldUrl = mUrls[mUrlSelected.first]
            mUrls[mUrlSelected.first] = Url(oldUrl.url, false)
        }
        mUrls[position] = Url(item.url, true)
        updateUi()
    }

    override fun onDeleteUrl(item: Url) {
        mUrls.remove(item)
        updateUi()
    }

    private fun getUrlSelected() {
        mUrlSelected = Pair(-1, "")
        mUrls.forEachIndexed { index, url ->
            if (url.isChecked) {
                mUrlSelected = Pair(index, url.url)
            }
        }
    }

    private fun updateUi() {
        getUrlSelected()

        mUrlAdapter.updateAllData(mUrls)
        textContentUrl.text = mUrlSelected.second
    }
}
