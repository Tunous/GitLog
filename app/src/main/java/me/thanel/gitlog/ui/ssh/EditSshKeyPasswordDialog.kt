package me.thanel.gitlog.ui.ssh

import activitystarter.Arg
import android.content.SharedPreferences
import android.os.Bundle
import com.marcinmoskala.activitystarter.argExtra
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.dialog.InputDialog

class EditSshKeyPasswordDialog : InputDialog() {
    private lateinit var prefs: SharedPreferences

    @get:Arg
    val keyName: String by argExtra()

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
}
