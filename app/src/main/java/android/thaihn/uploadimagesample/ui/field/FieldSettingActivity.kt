package android.thaihn.uploadimagesample.ui.field

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.thaihn.uploadimagesample.R
import android.thaihn.uploadimagesample.base.BaseActivity
import android.thaihn.uploadimagesample.base.dialog.InputDialog
import android.thaihn.uploadimagesample.entity.Field
import android.thaihn.uploadimagesample.util.FieldUtil
import android.util.Log
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

    private val mFields = mutableListOf<Field>()
    private var mFieldEditing: Pair<Field, Int>? = null

    private lateinit var mInputDialog: InputDialog

    override fun initComponent(savedInstanceState: Bundle?) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycleField.apply {
            adapter = mFieldAdapter
            layoutManager = LinearLayoutManager(applicationContext, RecyclerView.VERTICAL, false)
        }

        mFields.addAll(FieldUtil.getFields())
        mFieldAdapter.updateAllData(mFields)
        updateUi(mFields)

        mInputDialog = InputDialog(this, R.style.CommonDialog)
        mInputDialog.setOnInputListener(object : InputDialog.OnInputDialog {
            override fun onInputOk(text: String) {
                mFieldEditing?.let {
                    Log.d(TAG, "InputOk: item:${it.first} -- index:${it.second} -- text:$text")
                    mFields.set(it.second, Field(text, it.first.isChecked))
                    FieldUtil.saveFields(mFields)
                    mFieldAdapter.updateAllData(mFields)
                    updateUi(mFields)
                }
            }
        })

        buttonAdd.setOnClickListener {
            val textField = editTextField.text.toString().trim()
            if (textField.isNotEmpty()) {
                mFields.add(Field(textField, false))
                mFieldAdapter.addData(Field(textField, false))
                FieldUtil.saveFields(mFields)
                editTextField.text.clear()
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
    override fun onDeleteField(index: Int) {
        Log.d(TAG, "onDeleteField: $index")
        mFields.removeAt(index)
        mFieldAdapter.updateAllData(mFields)
        updateUi(mFields)
    }

    // Selected item
    override fun onSelected(item: Field, position: Int, isChecked: Boolean) {
        Log.d(TAG, "onSelected: position:$position -- $item:$item -- isChecked: $isChecked")
        mFields[position] = Field(item.title, isChecked)
        updateUi(mFields)
    }

    // Edit item
    override fun onEditField(item: Field, index: Int) {
        Log.d(TAG, "onEditField: item:$item -- index:$index")
        mFieldEditing = Pair(mFields[index], index)
        mInputDialog.mTextEdit = item.title
        mInputDialog.show()
    }

    private fun getFieldSelected(fields: List<Field>): ArrayList<String> {
        val selected = arrayListOf<String>()
        fields.forEach {
            if (it.isChecked) {
                selected.add(it.title)
            }
        }
        return selected
    }

    private fun updateUi(fields: List<Field>) {
        val fieldSelected = getFieldSelected(fields)
        textContentFields.text = fieldSelected.toString()
    }
}
