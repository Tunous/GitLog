package me.thanel.gitlog.ssh

import android.content.Context
import android.content.SharedPreferences
import org.eclipse.jgit.transport.CredentialItem
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.URIish

class SharedPreferencesCredentialsProvider(context: Context) : CredentialsProvider() {
    private val prefs = getPrefs(context)

    override fun isInteractive() = false

    override fun get(uri: URIish, vararg items: CredentialItem): Boolean {
        for (item in items) {
            if (item is CredentialItem.StringType) {
                val prompt = item.promptText
                val keyFileName = prompt.drop(prompt.lastIndexOf("/") + 1)
                val password = prefs.getString(keyFileName, null)
                if (password != null) {
                    item.value = password
                    return true
                }
            }
        }
        return false
    }

    override fun supports(vararg items: CredentialItem?) = true

    companion object {
        fun getPrefs(context: Context): SharedPreferences =
            context.getSharedPreferences("ssh_credential", Context.MODE_PRIVATE)
    }
}