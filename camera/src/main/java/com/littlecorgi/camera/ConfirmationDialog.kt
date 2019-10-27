package com.littlecorgi.camera

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment

class ConfirmationDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(activity)
                    .setMessage("This sample needs camera permission.")
                    .setPositiveButton(android.R.string.ok) { _, _ ->
                        parentFragment!!.requestPermissions(arrayOf(Manifest.permission.CAMERA),
                                REQUEST_CAMERA_PERMISSION)
                    }
                    .setNegativeButton(android.R.string.cancel) { _, _ ->
                        parentFragment!!.activity?.finish()
                    }
                    .create()
}
