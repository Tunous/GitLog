package me.thanel.gitlog.ui.ssh

import android.content.Context
import org.eclipse.jgit.api.TransportConfigCallback
import org.eclipse.jgit.transport.SshTransport
import org.eclipse.jgit.transport.Transport

class TransportCallback(context: Context) : TransportConfigCallback {
    private val ssh = SessionFactory(context)

    override fun configure(transport: Transport) {
        if (transport is SshTransport) {
            transport.sshSessionFactory = ssh
        }
    }
}
