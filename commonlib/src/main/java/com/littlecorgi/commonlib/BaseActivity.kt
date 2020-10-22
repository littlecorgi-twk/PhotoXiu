package com.littlecorgi.commonlib

import android.content.Context
import android.content.DialogInterface
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.yanzhenjie.permission.AndPermission

open class BaseActivity : AppCompatActivity() {

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

    /**
     * 通过AndPermission去获取权限
     *
     * @param context 传入的context
     * @param deniedPermissions 需要请求的权限
     * @param denied 请求失败的执行方法，如果不传入，则通过Toast显示权限获取失败
     * @param granted 请求成功的执行方法
     */
    @JvmOverloads
    protected fun requestPermission(context: Context, deniedPermissions: Array<String>, denied: ((data: List<String>) -> Unit)? = null, granted: (data: List<String>) -> Unit) {
        val deniedTemp = denied?.let {
            denied
        } ?: {
            makeShortToast("$it 权限获取失败，请检查")
        }

        AndPermission.with(this)
                .runtime()
                .permission(
                        deniedPermissions
                )
                .onGranted(granted)
                .onDenied(deniedTemp)
                .start()
    }
}
