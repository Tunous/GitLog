package me.thanel.gitlog.ui.ssh

import com.jcraft.jsch.KeyPair

data class SshKey(val name: String, val keyPair: KeyPair)
