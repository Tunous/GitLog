package me.thanel.gitlog.ssh

import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.jcraft.jsch.JSch
import com.jcraft.jsch.KeyPair
import kotlinx.android.synthetic.main.view_recycler.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.base.dialog.InputDialog
import me.thanel.gitlog.explorer.FileListAdapter
import me.thanel.gitlog.explorer.OnSshKeyMenuItemClickListener
import me.thanel.gitlog.utils.StyleableTag
import me.thanel.gitlog.utils.formatTags
import me.thanel.gitlog.utils.getViewModel
import me.thanel.gitlog.utils.sshDir
import me.thanel.gitlog.utils.sshPublicDir
import java.io.File
import java.io.FileOutputStream

class SshPrivateKeyListFragment : BaseFragment<SshPrivateKeyListViewModel>(),
        OnSshKeyMenuItemClickListener {
    private lateinit var rootFolder: File
    private lateinit var adapter: FileListAdapter

    override val layoutResId: Int
        get() = R.layout.view_recycler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (savedInstanceState != null) {
            val dialog = fragmentManager.findFragmentByTag(TAG_DIALOG_RENAME) as? InputDialog
            dialog?.setOnSubmitListener(this::refresh)
        }
    }

    override fun onCreateViewModel() = getViewModel<SshPrivateKeyListViewModel>(activity)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rootFolder = context.sshDir
        adapter = FileListAdapter(this)

        recyclerView.apply {
            adapter = this@SshPrivateKeyListFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }

        refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.ssh_key_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.generate_ssh_key -> generateSshKey()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onRename(key: File) {
        RenameSshKeyDialog.newInstance(key.name)
                .setOnSubmitListener(this::refresh)
                .show(fragmentManager, TAG_DIALOG_RENAME)
    }

    override fun onShowPublicKey(key: File) {
        TODO()
    }

    override fun onShowPrivateKey(key: File) {
        TODO()
    }

    override fun onEditPassword(key: File) {
        TODO()
    }

    override fun onRemove(key: File) {
        val message = getString(R.string.remove_confirm_message)
                .formatTags(StyleableTag("target", key.name, StyleSpan(Typeface.BOLD)))

        AlertDialog.Builder(context)
                .setTitle(R.string.remove_ssh_key)
                .setMessage(message)
                .setPositiveButton(R.string.remove) { _, _ ->
                    removeSshKey(key.name)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    private fun removeSshKey(name: String) {
        File(context.sshPublicDir, name).delete()
        File(context.sshDir, name).delete()
        refresh()
    }

    private fun refresh() {
        adapter.replaceAll(rootFolder.listFiles().asIterable())
    }

    private fun generateSshKey() {
        val fileName = "test"
        val privateKey = File(context.sshDir, fileName)
        val publicKey = File(context.sshPublicDir, fileName)

        val jsch = JSch()
        with(KeyPair.genKeyPair(jsch, KeyPair.RSA)) {
            writePrivateKey(FileOutputStream(privateKey))
            writePublicKey(FileOutputStream(publicKey), "GitLog")
            dispose()
        }

        refresh()
    }

    companion object {
        private const val TAG_DIALOG_RENAME = "dialog.rename"

        fun newInstance(): SshPrivateKeyListFragment = SshPrivateKeyListFragment()
    }
}