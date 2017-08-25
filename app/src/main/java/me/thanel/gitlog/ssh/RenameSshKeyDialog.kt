package me.thanel.gitlog.ssh

import me.thanel.gitlog.R
import me.thanel.gitlog.base.dialog.InputDialog
import me.thanel.gitlog.utils.sshDir
import me.thanel.gitlog.utils.stringArg
import me.thanel.gitlog.utils.withArguments
import java.io.File

class RenameSshKeyDialog : InputDialog() {
    private val currentKeyName by stringArg(ARG_CURRENT_KEY_NAME)

    override val titleResId get() = R.string.rename_ssh_key
    override val hintResId get() = R.string.new_ssh_key_name
    override val inputText get() = currentKeyName

    override fun onSubmit(input: String): Boolean {
        val newKeyName = input.trim()
        val privateKeyFile = File(context.sshDir, currentKeyName)
        val publicKeyFile = File(context.sshDir, "$currentKeyName.pub")
        val newPrivateKeyFile = File(context.sshDir, newKeyName)
        val newPublicKeyFile = File(context.sshDir, "$newKeyName.pub")

        val publicKeyRenameSuccess = publicKeyFile.renameTo(newPublicKeyFile)
        val privateKeyRenameSuccess = privateKeyFile.renameTo(newPrivateKeyFile)

        if (!publicKeyRenameSuccess || !privateKeyRenameSuccess) {
            showError("Failed renaming key...")
            return false
        }

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
        val newPrivateKeyFile = File(context.sshDir, newKeyName)
        val newPublicKeyFile = File(context.sshDir, "$newKeyName.pub")
        if (newPublicKeyFile.exists() || newPrivateKeyFile.exists()) {
            showError("SSH Key with this name already exists")
            return false
        }

        return super.validateInput(input)
    }

    companion object {
        private const val ARG_CURRENT_KEY_NAME = "arg.current_key_name"

        fun newInstance(currentKeyName: String) = RenameSshKeyDialog().withArguments {
            putString(ARG_CURRENT_KEY_NAME, currentKeyName)
        }
    }
}
