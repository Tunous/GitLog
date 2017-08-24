package me.thanel.gitlog.explorer

import android.graphics.Color
import android.support.v7.widget.PopupMenu
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.item_file.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter
import me.thanel.gitlog.utils.setItemTextColor
import java.io.File

class FileListAdapter(
        private val onSshKeyMenuItemClickListener: OnSshKeyMenuItemClickListener
) : ItemAdapter<File, FileListAdapter.ViewHolder>() {
    override fun getLayoutResId(viewType: Int) = R.layout.item_file

    override fun createViewHolder(itemView: View, viewType: Int) =
        ViewHolder(itemView, onSshKeyMenuItemClickListener)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    class ViewHolder(
            itemView: View,
            private val onSshKeyMenuItemClickListener: OnSshKeyMenuItemClickListener
    ) : ItemAdapter.ViewHolder<File>(itemView),
            PopupMenu.OnMenuItemClickListener {
        private val fileNameView = itemView.fileNameView
        private val popupMenu = PopupMenu(context, itemView, Gravity.END).apply {
            inflate(R.menu.ssh_key)
            menu.setItemTextColor(R.id.delete, Color.RED)
            setOnMenuItemClickListener(this@ViewHolder)
        }
        private var boundItem: File? = null

        init {
            itemView.setOnClickListener {
                popupMenu.show()
            }
        }

        override fun bind(item: File) {
            super.bind(item)
            boundItem = item
            fileNameView.text = item.name

            val iconResId = if (item.isDirectory) R.drawable.ic_folder else R.drawable.ic_file
            fileNameView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0)
        }

        override fun onMenuItemClick(item: MenuItem): Boolean {
            val key = boundItem ?: throw IllegalStateException(
                    "Selected menu action for non-existing key")

            with(onSshKeyMenuItemClickListener) {
                when (item.itemId) {
                    R.id.rename -> onRename(key)
                    R.id.show_public_key -> onShowPublicKey(key)
                    R.id.show_private_key -> onShowPrivateKey(key)
                    R.id.edit_password -> onEditPassword(key)
                    R.id.delete -> onDelete(key)
                    else -> return false
                }
            }

            return true
        }
    }
}

interface OnSshKeyMenuItemClickListener {
    fun onRename(key: File)
    fun onShowPublicKey(key: File)
    fun onShowPrivateKey(key: File)
    fun onEditPassword(key: File)
    fun onDelete(key: File)
}