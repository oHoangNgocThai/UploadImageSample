package android.thaihn.uploadimagesample.ui.field

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.thaihn.uploadimagesample.R
import android.thaihn.uploadimagesample.base.BaseActivity
import android.thaihn.uploadimagesample.entity.Field
import android.thaihn.uploadimagesample.util.FieldUtil
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_field_setting.*

class FieldSettingActivity : BaseActivity(), FieldAdapter.FieldListener {

    companion object {
        private val TAG = FieldSettingActivity::class.java.simpleName
    }

    override val layoutResource: Int
        get() = R.layout.activity_field_setting

    private val mFieldAdapter = FieldAdapter(this)

    private var mFields = arrayListOf<Field>()
    private var mFieldSelected = arrayListOf<String>()

    override fun initComponent(savedInstanceState: Bundle?) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycleField.apply {
            adapter = mFieldAdapter
            layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        }

        mFields = FieldUtil.getFields()
        updateUi()

        buttonAdd.setOnClickListener {
            val textField = editTextField.text.toString().trim()
            if (textField.isNotEmpty()) {
                mFields.add(Field(textField, false))
                editTextField.text.clear()
                updateUi()
            } else {
                Toast.makeText(applicationContext, "Field is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.menu_save -> {
                FieldUtil.saveFields(mFields)
                finish()
            }
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        FieldUtil.saveFields(mFields)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // Delete item
    override fun onDeleteField(item: Field) {
        mFields.remove(item)
        updateUi()
    }

    // Selected item
    override fun onSelected(item: Field, position: Int, isChecked: Boolean) {
        mFields[position] = Field(item.title, isChecked)
        updateUi()
    }

    private fun getFieldSelected() {
        mFieldSelected.clear()
        mFields.forEach {
            if (it.isChecked) {
                mFieldSelected.add(it.title)
            }
        }
    }

    private fun updateUi() {
        getFieldSelected()

        mFieldAdapter.updateAllData(mFields)
        textContentFields.text = mFieldSelected.toString()
    }
}
