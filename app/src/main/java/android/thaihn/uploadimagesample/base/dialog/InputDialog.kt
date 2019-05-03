package android.thaihn.uploadimagesample.base.dialog

import android.content.Context
import android.thaihn.uploadimagesample.R
import android.view.LayoutInflater
import kotlinx.android.synthetic.main.input_dialog.*

class InputDialog : BaseDialog {

    interface OnInputDialog {
        fun onInputOk(text: String)
    }

    constructor(context: Context?) : super(context)

    constructor(context: Context?, themeResId: Int) : super(context, themeResId)


    private var onInputListener: OnInputDialog? = null

    var mTextEdit: String = ""

    fun setOnInputListener(onListener: OnInputDialog?) {
        onInputListener = onListener
    }

    override val isCancelable: Boolean
        get() = false

    override fun initView() {
        val view = LayoutInflater.from(context).inflate(R.layout.input_dialog, null, false)
        setContentView(view)

        textOk.setOnClickListener {
            dismiss()
            onInputListener?.onInputOk(editTextInput.text.toString().trim())
        }

        textCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun show() {
        super.show()
        if(mTextEdit.isNotBlank()) {
            editTextInput.setText(mTextEdit)
        }
    }
}
