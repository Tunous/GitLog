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
import me.thanel.gitlog.explorer.FileViewerActivity
import me.thanel.gitlog.explorer.OnSshKeyMenuItemClickListener
import me.thanel.gitlog.explorer.SshKeyListAdapter
import me.thanel.gitlog.utils.StyleableTag
import me.thanel.gitlog.utils.formatTags
import me.thanel.gitlog.utils.getViewModel
import me.thanel.gitlog.utils.sshPrivateDir
import me.thanel.gitlog.utils.sshPublicDir
import java.io.File
import java.io.FileOutputStream

class SshKeyManagementFragment : BaseFragment<SshKeyManagementViewModel>(),
        OnSshKeyMenuItemClickListener {
    private lateinit var rootFolder: File
    private lateinit var adapter: SshKeyListAdapter

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

    override fun onCreateViewModel() = getViewModel<SshKeyManagementViewModel>(activity)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rootFolder = context.sshPrivateDir
        adapter = SshKeyListAdapter(this)

        recyclerView.apply {
            adapter = this@SshKeyManagementFragment.adapter
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

    override fun onRenameKey(name: String) {
        RenameSshKeyDialog.newInstance(name)
                .setOnSubmitListener(this::refresh)
                .show(fragmentManager, TAG_DIALOG_RENAME)
    }

    override fun onShowPublicKey(name: String) {
        showFileContent(File(context.sshPublicDir, name))
    }

    override fun onShowPrivateKey(name: String) {
        showFileContent(File(context.sshPrivateDir, name))
    }

    override fun onEditKeyPassword(name: String) {
        TODO()
    }

    override fun onRemoveKey(name: String) {
        val message = getString(R.string.remove_confirm_message)
                .formatTags(StyleableTag("target", name, StyleSpan(Typeface.BOLD)))

        AlertDialog.Builder(context)
                .setTitle(R.string.remove_ssh_key)
                .setMessage(message)
                .setPositiveButton(R.string.remove) { _, _ ->
                    removeSshKey(name)
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    private fun removeSshKey(name: String) {
        File(context.sshPublicDir, name).delete()
        File(context.sshPrivateDir, name).delete()
        refresh()
    }

    private fun refresh() {
        val files = rootFolder.listFiles()
        val jSch = JSch()
        val keys = files.map { it.name to KeyPair.load(jSch, it.absolutePath) }
                .sortedBy(Pair<String, KeyPair>::first)
        adapter.replaceAll(keys)
    }

    private fun generateSshKey() {
        val fileName = "test"
        val privateKey = File(context.sshPrivateDir, fileName)
        val publicKey = File(context.sshPublicDir, fileName)

        val jsch = JSch()
        with(KeyPair.genKeyPair(jsch, KeyPair.RSA)) {
            writePrivateKey(FileOutputStream(privateKey))
            writePublicKey(FileOutputStream(publicKey), "GitLog")
            dispose()
        }

        refresh()
    }

    private fun showFileContent(file: File) {
        val intent = FileViewerActivity.newIntent(context, file.absolutePath)
        startActivity(intent)
    }

    companion object {
        private const val TAG_DIALOG_RENAME = "dialog.rename"

        fun newInstance(): SshKeyManagementFragment = SshKeyManagementFragment()
    }
}