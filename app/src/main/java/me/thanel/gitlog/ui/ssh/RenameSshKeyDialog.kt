package me.thanel.gitlog.ui.ssh

import activitystarter.Arg
import com.marcinmoskala.activitystarter.argExtra
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.dialog.InputDialog
import me.thanel.gitlog.ui.utils.sshDir
import java.io.File

class RenameSshKeyDialog : InputDialog() {
    @get:Arg
    val currentKeyName: String by argExtra()

    override val titleResId get() = R.string.rename_ssh_key
    override val hintResId get() = R.string.new_ssh_key_name
    override val inputText get() = currentKeyName
    override val positiveButtonResId get() = R.string.rename

    override fun onSubmit(input: String): Boolean {
        val newKeyName = input.trim()
        val privateKeyFile = File(requireContext().sshDir, currentKeyName)
        val publicKeyFile = File(requireContext().sshDir, "$currentKeyName.pub")
        val newPrivateKeyFile = File(requireContext().sshDir, newKeyName)
        val newPublicKeyFile = File(requireContext().sshDir, "$newKeyName.pub")

        val publicKeyRenameSuccess = publicKeyFile.renameTo(newPublicKeyFile)
        val privateKeyRenameSuccess = privateKeyFile.renameTo(newPrivateKeyFile)

        if (!publicKeyRenameSuccess || !privateKeyRenameSuccess) {
            showError("Failed renaming key...")
            return false
        }

        val prefs = SharedPreferencesCredentialsProvider.getPrefs(requireContext())
        val password = prefs.getString(currentKeyName, null)
        prefs.edit()
            .putString(newKeyName, password)
            .apply()

        return true
    }

    override fun validateInput(input: CharSequence): Boolean {
        if (input.isBlank()) {
            showError("Key name required")
            return false
        }

        if (input.contains("/") || input.contains(".")) {
            showError("Key name cannot contain / or . characters")
            return false
        }

        val newKeyName = input.trim().toString()
        val newPrivateKeyFile = File(requireContext().sshDir, newKeyName)
        val newPublicKeyFile = File(requireContext().sshDir, "$newKeyName.pub")
        if (newPublicKeyFile.exists() || newPrivateKeyFile.exists()) {
            showError("SSH Key with this name already exists")
            return false
        }

        return super.validateInput(input)
    }
}
