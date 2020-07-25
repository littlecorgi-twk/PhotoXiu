package com.littlecorgi.commonlib

import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private var mAlertDialog: AlertDialog? = null

    override fun onStop() {
        super.onStop()
        if (mAlertDialog != null && mAlertDialog!!.isShowing) {
            mAlertDialog!!.dismiss()
        }
    }

    fun makeShortToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    fun makeLongToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    /**
     * This method shows dialog with given title & message.
     * Also there is an option to pass onClickListener for positive & negative button.
     *
     * @param title                         - dialog title
     * @param message                       - dialog message
     * @param onPositiveButtonClickListener - listener for positive button
     * @param positiveText                  - positive button text
     * @param onNegativeButtonClickListener - listener for negative button
     * @param negativeText                  - negative button text
     */
    protected fun showAlertDialog(title: String?, message: String?,
                                  onPositiveButtonClickListener: DialogInterface.OnClickListener?,
                                  positiveText: String,
                                  onNegativeButtonClickListener: DialogInterface.OnClickListener?,
                                  negativeText: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener)
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener)
        mAlertDialog = builder.show()
    }
}
