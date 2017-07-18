package me.thanel.gitlog

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_repository.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.eclipse.jgit.api.Git
import java.io.File

class AddRepositoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_repository)

        cloneButton.setOnClickListener {
            val uri = repositoryUrlInputView.editText?.text?.toString()
            if (uri != null) {
                cloneRepository(uri)
            } else {
                repositoryUrlInputView.error = "Please enter url"
            }
        }
    }

    private fun cloneRepository(uri: String) {
        val regex = Regex("""https://github.com/([^/]+)/([^/]+)""")
        val uriResult = regex.matchEntire(uri)

        if (uriResult == null) {
            repositoryUrlInputView.error = "Incorrect url"
            return
        }

        val targetName = repositoryNameInputView.editText?.text?.toString()
        if (targetName == null || targetName.isBlank()) {
            repositoryNameInputView.error = "Incorrect name"
            return
        }

        val repoOwner = uriResult.groupValues[1]
        val repoName = uriResult.groupValues[2]

        launch(UI) {
            val dialog = ProgressDialog.show(this@AddRepositoryActivity, "Cloning repository",
                    "Cloning \"$repoOwner/$repoName\" as $targetName...", true)

            val result = async(CommonPool) {
                val rootFile = File(filesDir, "repos/$targetName")

                Git.cloneRepository()
                        .setURI(uri)
                        .setDirectory(rootFile)
                        .setBare(true)
                        .setCloneAllBranches(true)
                        .setRemote("origin")
                        .call()
            }
            result.await()

            dialog.dismiss()

            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(RepositoryListActivity.EXTRA_REPOSITORY, Repository(targetName))
            })
            finish()
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, AddRepositoryActivity::class.java)
        }
    }
}
