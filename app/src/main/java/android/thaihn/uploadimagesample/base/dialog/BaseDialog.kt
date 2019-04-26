package android.thaihn.uploadimagesample.base.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window

abstract class BaseDialog : Dialog {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, themeId: Int) : super(context, themeId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setBackgroundDrawableResource(android.R.color.transparent)
        setCancelable(isCancelable)
        initView()
    }

    protected abstract val isCancelable: Boolean

    protected abstract fun initView()
}
