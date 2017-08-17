package me.thanel.gitlog.ssh

import android.content.Context
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.CredentialsProviderUserInfo
import org.eclipse.jgit.transport.JschConfigSessionFactory
import org.eclipse.jgit.transport.OpenSshConfig
import org.eclipse.jgit.util.FS
import java.io.File

class SessionFactory(private val context: Context) : JschConfigSessionFactory() {
    override fun configure(host: OpenSshConfig.Host, session: Session) {
        session.setConfig("StrictHostKeyChecking", "no")
        session.setConfig("PreferredAuthentications", "publickey,password")

        session.userInfo = CredentialsProviderUserInfo(session, CredentialsProvider.getDefault())
    }

    override fun createDefaultJSch(fs: FS): JSch {
        val jsch = JSch()
        val sshDir = File(context.filesDir, "ssh")
        if (!sshDir.exists()) {
            sshDir.mkdir()
        }
        for (file in sshDir.listFiles()) {
            jsch.addIdentity(file.absolutePath)
        }
        return jsch
    }
}