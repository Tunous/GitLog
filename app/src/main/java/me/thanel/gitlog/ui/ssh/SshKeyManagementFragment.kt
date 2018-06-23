package me.thanel.gitlog.ui.ssh

import activitystarter.MakeActivityStarter
import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.KeyPair
import kotlinx.android.synthetic.main.view_recycler.*
import me.drakeet.multitype.MultiTypeAdapter
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.dialog.InputDialog
import me.thanel.gitlog.ui.base.fragment.BaseFragment
import me.thanel.gitlog.ui.explorer.FileViewerActivityStarter
import me.thanel.gitlog.ui.filepicker.FilePickerActivity
import me.thanel.gitlog.ui.filepicker.FilePickerActivityStarter
import me.thanel.gitlog.ui.utils.StyleableTag
import me.thanel.gitlog.ui.utils.formatTags
import me.thanel.gitlog.ui.utils.sshDir
import java.io.File

@MakeActivityStarter
class SshKeyManagementFragment : BaseFragment(),
    OnSshKeyMenuItemClickListener {
    private lateinit var rootFolder: File
    private val adapter = MultiTypeAdapter().apply {
        register(SshKey::class.java, SshKeyViewBinder(this@SshKeyManagementFragment))
    }

    override val layoutResId: Int
        get() = R.layout.view_recycler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if (savedInstanceState != null) {
            (requireFragmentManager().findFragmentByTag(TAG_DIALOG_RENAME) as? InputDialog)
                ?.setOnSubmitListener(this::refresh)

            (requireFragmentManager().findFragmentByTag(TAG_DIALOG_GENERATE_KEY) as? InputDialog)
                ?.setOnSubmitListener(this::refresh)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rootFolder = requireContext().sshDir

        recyclerView.adapter = adapter

        refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.ssh_key_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.generate_ssh_key -> generateSshKey()
            R.id.import_ssh_key -> importSshKey()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
    }

    override fun onRenameKey(name: String) {
        RenameSshKeyDialogStarter.newInstance(name)
            .setOnSubmitListener(this::refresh)
            .show(fragmentManager, TAG_DIALOG_RENAME)
    }

    override fun onShowPublicKey(name: String) {
        val publicKey = File(requireContext().sshDir, "$name.pub")
        if (!publicKey.exists()) {
            // TODO: Do not show menu entry for this action if key doesn't exist
            Toast.makeText(context, "Public key doesn't exist", Toast.LENGTH_SHORT).show()
            return
        }
        showFileContent(publicKey)
    }

    override fun onShowPrivateKey(name: String) {
        showFileContent(File(requireContext().sshDir, name))
    }

    override fun onEditKeyPassword(name: String) {
        EditSshKeyPasswordDialogStarter.newInstance(name)
            .show(fragmentManager, TAG_DIALOG_EDIT_PASSWORD)
    }

    override fun onRemoveKey(name: String) {
        val message = getString(R.string.remove_confirm_message)
            .formatTags(StyleableTag("target", name, StyleSpan(Typeface.BOLD)))

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.remove_ssh_key)
            .setMessage(message)
            .setPositiveButton(R.string.remove) { _, _ ->
                removeSshKey(name)
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_PICK_FILE) {
            if (resultCode == Activity.RESULT_OK) {
                val filePath = data!!.getStringExtra(FilePickerActivity.EXTRA_FILE_PATH)
                val keyFile = File(filePath)
                val newKey = File(requireContext().sshDir, keyFile.name)
                keyFile.copyTo(newKey)
                refresh()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun removeSshKey(name: String) {
        File(requireContext().sshDir, name).delete()
        File(requireContext().sshDir, "$name.pub").delete()

        SharedPreferencesCredentialsProvider.getPrefs(requireContext())
            .edit()
            .remove(name)
            .apply()

        refresh()
    }

    private fun refresh() {
        val files = rootFolder.listFiles()
            .filterNot { it.startsWith(".pub") }
        val jSch = JSch()
        val keys = mutableListOf<SshKey>()
        for (file in files) {
            val keyPair = try {
                KeyPair.load(jSch, file.absolutePath)
            } catch (ex: JSchException) {
                null
            }
            if (keyPair != null) {
                keys.add(SshKey(file.name, keyPair))
            }
        }
        adapter.items = keys.sortedBy(SshKey::name)
        adapter.notifyDataSetChanged()
    }

    private fun generateSshKey() {
        GenerateSshKeyDialog.newInstance()
            .setOnSubmitListener(this::refresh)
            .show(fragmentManager, TAG_DIALOG_GENERATE_KEY)
    }

    private fun showFileContent(file: File) {
        FileViewerActivityStarter.start(requireContext(), file.absolutePath)
    }

    private fun importSshKey() {
        FilePickerActivityStarter.startForResult(requireActivity(), REQUEST_PICK_FILE)
    }

    companion object {
        private const val TAG_DIALOG_RENAME = "dialog.rename"
        private const val TAG_DIALOG_EDIT_PASSWORD = "dialog.edit_password"
        private const val TAG_DIALOG_GENERATE_KEY = "dialog.generate_key"
        private const val REQUEST_PICK_FILE = 1
    }
}
