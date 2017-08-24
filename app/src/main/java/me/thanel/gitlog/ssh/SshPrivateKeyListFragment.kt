package me.thanel.gitlog.ssh

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.jcraft.jsch.JSch
import com.jcraft.jsch.KeyPair
import kotlinx.android.synthetic.main.view_recycler.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.explorer.FileListAdapter
import me.thanel.gitlog.utils.getViewModel
import me.thanel.gitlog.utils.sshDir
import me.thanel.gitlog.utils.sshPublicDir
import java.io.File
import java.io.FileOutputStream

class SshPrivateKeyListFragment : BaseFragment<SshPrivateKeyListViewModel>() {
    private lateinit var rootFolder: File
    private lateinit var adapter: FileListAdapter

    override val layoutResId: Int
        get() = R.layout.view_recycler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateViewModel() = getViewModel<SshPrivateKeyListViewModel>(activity)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        rootFolder = context.sshDir
        adapter = FileListAdapter()

        recyclerView.apply {
            adapter = this@SshPrivateKeyListFragment.adapter
            layoutManager = LinearLayoutManager(context)
        }

        refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.ssh_private_key_list, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.generate_ssh_key -> generateSshKey()
            else -> return super.onOptionsItemSelected(item)
        }

        return true
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
        fun newInstance(): SshPrivateKeyListFragment = SshPrivateKeyListFragment()
    }
}