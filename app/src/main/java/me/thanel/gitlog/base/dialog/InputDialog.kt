package me.thanel.gitlog.base.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.design.widget.TextInputLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import kotlinx.android.synthetic.main.dialog_input.view.*
import me.thanel.gitlog.R

abstract class InputDialog : DialogFragment() {
    private lateinit var inputField: EditText
    private lateinit var inputLayout: TextInputLayout
    private var onSubmitListener: (() -> Unit)? = null

    protected open val titleResId = 0
    protected open val hintResId = 0
    protected open val inputText: String? = null
    protected open val isPasswordInput = false
    protected abstract val positiveButtonResId: Int

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(context, R.layout.dialog_input, null)
        inputLayout = view.dialogInputTextLayout
        inputField = view.dialogInputField.apply {
            setHint(hintResId)
            setText(inputText)
            if (isPasswordInput) {
                inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(p0: Editable?) = Unit

                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit

                override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                    validateInput(text)
                }
            })
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(titleResId)
            .setView(view)
            .setPositiveButton(positiveButtonResId, null)
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    override fun onResume() {
        super.onResume()
        val dialog = dialog as? AlertDialog ?: return

        val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
        positiveButton.setOnClickListener {
            val input = inputField.text.toString()
            if (!validateInput(input)) return@setOnClickListener

            if (onSubmit(input)) {
                onSubmitListener?.invoke()
                dialog.dismiss()
            }
        }
    }

    fun setOnSubmitListener(listener: (() -> Unit)?): InputDialog {
        onSubmitListener = listener
        return this
    }

    protected fun showError(error: String) {
        inputLayout.error = error
        inputLayout.isErrorEnabled = true
    }

    protected abstract fun onSubmit(input: String): Boolean

    @CallSuper
    protected open fun validateInput(input: CharSequence): Boolean {
        inputLayout.isErrorEnabled = false
        return true
    }
}
