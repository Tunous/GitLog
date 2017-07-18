package me.thanel.gitlog

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.run
import org.eclipse.jgit.api.Git
import java.io.File

class MainActivity : AppCompatActivity() {

    private val adapter = CommitLogAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)

        val rootFile = File(filesDir, "repos/repo")

        if (!rootFile.exists()) {
            launch(UI) {
                val dialog = ProgressDialog.show(this@MainActivity, "Cloning repository",
                        "Cloning \"slapperwan/gh4a\"...", true)

                val result = run(CommonPool) {
                    Git.cloneRepository()
                            .setURI("https://github.com/slapperwan/gh4a.git")
                            .setDirectory(rootFile)
                            .setBare(true)
                            .setCloneAllBranches(true)
                            .setRemote("origin")
                            .call()
                }

                dialog.dismiss()

                onOpen(result)
            }
        } else {
            onOpen(Git.open(rootFile))
        }
    }

    private fun onOpen(git: Git) = launch(UI) {
        val log = run(CommonPool) { git.log().call() }
        adapter.addAll(log)
    }
}
