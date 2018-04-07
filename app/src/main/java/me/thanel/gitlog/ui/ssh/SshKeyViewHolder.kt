package me.thanel.gitlog.ui.ssh

import android.graphics.Color
import android.support.v7.widget.PopupMenu
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.view.isVisible
import kotlinx.android.synthetic.main.item_ssh_key.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.adapter.ItemViewHolder
import me.thanel.gitlog.ui.utils.setItemTextColor

class SshKeyViewHolder(
    itemView: View,
    private val onSshKeyMenuItemClickListener: OnSshKeyMenuItemClickListener
) : ItemViewHolder<SshKey>(itemView),
    PopupMenu.OnMenuItemClickListener {
    private val titleView = itemView.titleView
    private val fingerPrintView = itemView.fingerPrintView
    private val popupMenu = PopupMenu(
        context,
        itemView,
        Gravity.END
    ).apply {
        inflate(R.menu.ssh_key)
        menu.setItemTextColor(R.id.remove, Color.RED)
        setOnMenuItemClickListener(this@SshKeyViewHolder)
    }
    private var boundItem: String? = null

    init {
        itemView.setOnClickListener {
            popupMenu.show()
        }
    }

    override fun bind(item: SshKey) {
        super.bind(item)
        boundItem = item.name
        titleView.text = item.name
        fingerPrintView.text = item.keyPair.fingerPrint
        fingerPrintView.isVisible = !fingerPrintView.text.isNullOrBlank()
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        val keyName = boundItem ?: throw IllegalStateException(
            "Selected menu action for non-existing key"
        )

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
