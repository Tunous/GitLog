package me.thanel.gitlog.ssh

import me.thanel.gitlog.R
import me.thanel.gitlog.base.dialog.InputDialog
import me.thanel.gitlog.utils.sshPrivateDir
import me.thanel.gitlog.utils.sshPublicDir
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
        val publicKeyFile = File(context.sshPrivateDir, currentKeyName)
        val privateKeyFile = File(context.sshPublicDir, currentKeyName)
        val newPublicKeyFile = File(context.sshPrivateDir, newKeyName)
        val newPrivateKeyFile = File(context.sshPublicDir, newKeyName)

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

        if (input.contains("/")) {
            showError("Key name cannot contain / characters")
            return false
        }

        val newKeyName = input.trim().toString()
        val newPublicKeyFile = File(context.sshPrivateDir, newKeyName)
        val newPrivateKeyFile = File(context.sshPublicDir, newKeyName)
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
