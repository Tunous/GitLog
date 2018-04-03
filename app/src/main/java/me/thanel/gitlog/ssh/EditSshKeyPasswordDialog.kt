package me.thanel.gitlog.ssh

import android.content.SharedPreferences
import android.os.Bundle
import me.thanel.gitlog.R
import me.thanel.gitlog.base.dialog.InputDialog
import me.thanel.gitlog.utils.stringArg
import me.thanel.gitlog.utils.withArguments

class EditSshKeyPasswordDialog : InputDialog() {
    private lateinit var prefs: SharedPreferences
    private val keyName by stringArg(ARG_KEY_NAME)

    override val titleResId get() = R.string.edit_password
    override val hintResId get() = R.string.password
    override val isPasswordInput get() = true
    override val positiveButtonResId get() = R.string.edit
    override val inputText: String? get() = prefs.getString(keyName, null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = SharedPreferencesCredentialsProvider.getPrefs(requireContext())
    }

    override fun onSubmit(input: String): Boolean {
        SharedPreferencesCredentialsProvider.getPrefs(requireContext())
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
