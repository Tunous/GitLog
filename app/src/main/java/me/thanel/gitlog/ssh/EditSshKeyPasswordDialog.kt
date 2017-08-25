package me.thanel.gitlog.ssh

import me.thanel.gitlog.R
import me.thanel.gitlog.base.dialog.InputDialog
import me.thanel.gitlog.utils.stringArg
import me.thanel.gitlog.utils.withArguments

class EditSshKeyPasswordDialog : InputDialog() {
    private val keyName by stringArg(ARG_KEY_NAME)

    override val titleResId get() = R.string.edit_password
    override val hintResId get() = R.string.password
    override val isPasswordInput get() = true
    override val positiveButtonResId get() = R.string.edit

    override fun onSubmit(input: String): Boolean {
        SharedPreferencesCredentialsProvider.getPrefs(context)
                .edit()
                .putString(keyName, input)
                .apply()
        return true
    }

    companion object {
        private const val ARG_KEY_NAME = "arg.key_name"

        fun newInstance(currentKeyName: String) = EditSshKeyPasswordDialog().withArguments {
            putString(ARG_KEY_NAME, currentKeyName)
        }
    }
}