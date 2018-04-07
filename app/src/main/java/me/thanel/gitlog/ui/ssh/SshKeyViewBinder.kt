package me.thanel.gitlog.ui.ssh

import android.view.LayoutInflater
import android.view.ViewGroup
import me.drakeet.multitype.ItemViewBinder
import me.thanel.gitlog.R

class SshKeyViewBinder(
    private val onSshKeyMenuItemClickListener: OnSshKeyMenuItemClickListener
) : ItemViewBinder<SshKey, SshKeyViewHolder>() {
    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): SshKeyViewHolder {
        val itemView = inflater.inflate(R.layout.item_ssh_key, parent, false)
        return SshKeyViewHolder(itemView, onSshKeyMenuItemClickListener)
    }

    override fun onBindViewHolder(holder: SshKeyViewHolder, item: SshKey) {
        holder.bind(item)
    }
}
