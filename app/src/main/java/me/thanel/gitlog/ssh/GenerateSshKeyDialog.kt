package me.thanel.gitlog.ssh

import android.content.SharedPreferences
import android.os.Bundle
import com.jcraft.jsch.JSch
import com.jcraft.jsch.KeyPair
import me.thanel.gitlog.R
import me.thanel.gitlog.base.dialog.InputDialog
import me.thanel.gitlog.utils.sshDir
import java.io.File
import java.io.FileOutputStream

class GenerateSshKeyDialog : InputDialog() {
    private lateinit var prefs: SharedPreferences

    override val titleResId get() = R.string.generate
    override val hintResId get() = R.string.new_ssh_key_name
    override val positiveButtonResId get() = R.string.generate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = SharedPreferencesCredentialsProvider.getPrefs(requireContext())
    }

    override fun onSubmit(input: String): Boolean {
        val keyName = input.trim()
        val privateKey = File(requireContext().sshDir, keyName)
        val publicKey = File(requireContext().sshDir, "$keyName.pub")

        val jsch = JSch()
        with(KeyPair.genKeyPair(jsch, KeyPair.RSA)) {
            writePrivateKey(FileOutputStream(privateKey))
            writePublicKey(FileOutputStream(publicKey), getString(R.string.app_name))
            dispose()
        }

        return true
    }

    companion object {
        fun newInstance() = GenerateSshKeyDialog()
    }
}
