package me.thanel.gitlog.ssh

interface OnSshKeyMenuItemClickListener {
    fun onRenameKey(name: String)
    fun onShowPublicKey(name: String)
    fun onShowPrivateKey(name: String)
    fun onEditKeyPassword(name: String)
    fun onRemoveKey(name: String)
}