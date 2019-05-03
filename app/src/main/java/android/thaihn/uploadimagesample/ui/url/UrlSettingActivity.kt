package android.thaihn.uploadimagesample.ui.url

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.thaihn.uploadimagesample.R
import android.thaihn.uploadimagesample.base.BaseActivity
import android.thaihn.uploadimagesample.base.dialog.InputDialog
import android.thaihn.uploadimagesample.entity.Url
import android.thaihn.uploadimagesample.util.UrlUtil
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_url_setting.*

class UrlSettingActivity : BaseActivity(), UrlAdapter.UrlListener {

    companion object {
        private val TAG = UrlSettingActivity::class.java.simpleName
    }

    override val layoutResource: Int
        get() = R.layout.activity_url_setting

    private val mUrlAdapter = UrlAdapter(this)

    private lateinit var mInputDialog: InputDialog

    private var mUrls = arrayListOf<Url>()
    private var mUrlSelected = Pair(-1, "")

    private var mUrlEdit: Pair<Url, Int>? = null

    override fun initComponent(savedInstanceState: Bundle?) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycleUrl.apply {
            adapter = mUrlAdapter
            layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        }

        mUrls.addAll(UrlUtil.getUrls())
        mUrlAdapter.updateAllData(mUrls)
        updateUi(mUrls)

        mInputDialog = InputDialog(this, R.style.CommonDialog)
        mInputDialog.setOnInputListener(object : InputDialog.OnInputDialog {
            override fun onInputOk(text: String) {
                mUrlEdit?.let {
                    Log.d(TAG, "InputOk: item:${it.first} -- index:${it.second} -- text:$text")
                    mUrls[it.second] = Url(text, it.first.isChecked)
                    UrlUtil.saveUrls(mUrls)
                    mUrlAdapter.updateAllData(mUrls)
                    updateUi(mUrls)
                }
            }
        })

        buttonAdd.setOnClickListener {
            val url = editTextUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                mUrls.add(Url(url, false))
                mUrlAdapter.addData(Url(url, false))
                UrlUtil.saveUrls(mUrls)
                editTextUrl.text.clear()
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

    override fun onClickListener(index: Int) {
        Log.d(TAG, "onClickListener")
        if (mUrlSelected.first != -1) {
            val oldUrl = mUrls[mUrlSelected.first]
            mUrls[mUrlSelected.first] = Url(oldUrl.url, false)
        }
        mUrls[index] = Url(mUrls[index].url, true)
        mUrlAdapter.updateAllData(mUrls)
        updateUi(mUrls)
    }

    override fun onDeleteUrl(index: Int) {
        Log.d(TAG, "onDeleteUrl: item:${mUrls[index]} -- index:$index")
        mUrls.removeAt(index)
        mUrlAdapter.updateAllData(mUrls)
        updateUi(mUrls)
    }

    override fun onEditUrl(index: Int) {
        Log.d(TAG, "onEditUrl: index:$index")
        mUrlEdit = Pair(mUrls[index], index)
        mInputDialog.mTextEdit = mUrls[index].url
        mInputDialog.show()
    }

    private fun getUrlSelected(urls: List<Url>): Pair<Int, String> {
        var selected = Pair(-1, "")
        urls.forEachIndexed { index, url ->
            if (url.isChecked) {
                selected = Pair(index, url.url)
            }
        }
        return selected
    }

    private fun updateUi(urls: List<Url>) {
        val urlSelected = getUrlSelected(urls)
        mUrlSelected = urlSelected
        textContentUrl.text = urlSelected.second
    }
}
