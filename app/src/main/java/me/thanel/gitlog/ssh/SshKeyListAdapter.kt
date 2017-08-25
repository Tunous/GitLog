package me.thanel.gitlog.ssh

import android.graphics.Color
import android.support.v7.widget.PopupMenu
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import com.jcraft.jsch.KeyPair
import kotlinx.android.synthetic.main.item_ssh_key.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter
import me.thanel.gitlog.utils.setItemTextColor

class SshKeyListAdapter(
        private val onSshKeyMenuItemClickListener: OnSshKeyMenuItemClickListener
) : ItemAdapter<Pair<String, KeyPair>, SshKeyListAdapter.ViewHolder>() {
    override fun getLayoutResId(viewType: Int) = R.layout.item_ssh_key

    override fun createViewHolder(itemView: View, viewType: Int) =
        ViewHolder(itemView, onSshKeyMenuItemClickListener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(
            itemView: View,
            private val onSshKeyMenuItemClickListener: OnSshKeyMenuItemClickListener
    ) : ItemAdapter.ViewHolder<Pair<String, KeyPair>>(itemView),
            PopupMenu.OnMenuItemClickListener {
        private val titleView = itemView.titleView
        private val fingerPrintView = itemView.fingerPrintView
        private val popupMenu = PopupMenu(context, itemView, Gravity.END).apply {
            inflate(R.menu.ssh_key)
            menu.setItemTextColor(R.id.remove, Color.RED)
            setOnMenuItemClickListener(this@ViewHolder)
        }
        private var boundItem: String? = null

        init {
            itemView.setOnClickListener {
                popupMenu.show()
            }
        }

        override fun bind(item: Pair<String, KeyPair>) {
            super.bind(item)
            boundItem = item.first
            titleView.text = item.first
            fingerPrintView.text = item.second.fingerPrint
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            val keyName = boundItem ?: throw IllegalStateException(
                    "Selected menu action for non-existing key")

            with(onSshKeyMenuItemClickListener) {
                when (item.itemId) {
                    R.id.rename -> onRenameKey(keyName)
                    R.id.show_public_key -> onShowPublicKey(keyName)
                    R.id.show_private_key -> onShowPrivateKey(keyName)
                    R.id.edit_password -> onEditKeyPassword(keyName)
                    R.id.remove -> onRemoveKey(keyName)
                    else -> return false
                }
            }

            return true
        }
    }
}
