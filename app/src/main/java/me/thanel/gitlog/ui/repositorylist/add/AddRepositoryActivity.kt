package me.thanel.gitlog.ui.repositorylist.add

import activitystarter.MakeActivityStarter
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import kotlinx.android.synthetic.main.activity_add_repository.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import me.thanel.gitlog.R
import me.thanel.gitlog.db.model.GitLogRepository
import me.thanel.gitlog.ui.repositorylist.RepositoryListActivityStarter
import me.thanel.gitlog.ui.repositorylist.RepositoryListManager
import me.thanel.gitlog.ui.repositorylist.RepositoryListViewModel
import me.thanel.gitlog.ui.ssh.TransportCallback
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.InvalidRemoteException
import org.eclipse.jgit.api.errors.TransportException
import org.eclipse.jgit.lib.EmptyProgressMonitor
import org.koin.android.architecture.ext.viewModel
import java.io.File

@MakeActivityStarter
class AddRepositoryActivity : AppCompatActivity() {
    private val repositoryListViewModel by viewModel<RepositoryListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_repository)

        cloneButton.setOnClickListener { cloneRepository() }

        repositoryUrlInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val text = s.toString().split("/").lastOrNull() ?: ""
                val editable = repositoryNameInput.text
                editable.clear()
                editable.append(text)

                repositoryUrlInputView.error = null
            }

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })

        repositoryNameInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                repositoryNameInputView.error = null
            }

            override fun beforeTextChanged(
                s: CharSequence?, start: Int, count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        })

        repositoryNameInput.setOnEditorActionListener { _, actionId, event ->
            val pressedEnter = event.keyCode == KeyEvent.KEYCODE_ENTER &&
                    event.action == KeyEvent.ACTION_DOWN

            if (actionId == EditorInfo.IME_ACTION_SEND || pressedEnter) {
                cloneRepository()
                true
            } else {
                false
            }
        }
    }

    private fun cloneRepository() {
        val repoUrl = repositoryUrlInput.text.toString()
        val targetName = repositoryNameInput.text.toString()

        if (repoUrl.isBlank()) {
            repositoryUrlInputView.error = "Please specify URL"
            return
        } else {
            repositoryUrlInputView.error = null
        }

        if (targetName.isBlank()) {
            repositoryNameInputView.error = "Please specify name"
            return
        } else {
            repositoryNameInputView.error = null
        }

        val exists =
            RepositoryListManager.exists(this, targetName)
        if (exists) {
            repositoryNameInputView.error = "Repository with this name already exists"
            return
        } else {
            repositoryNameInputView.error = null
        }

        cloneRepository(repoUrl, targetName)
    }

    private fun cloneRepository(repoUrl: String, repoName: String) {
        val rootFile = File(filesDir, "repos/$repoName")

        launch(UI) {
            val dialog = ProgressDialog.show(
                this@AddRepositoryActivity, "Cloning repository",
                "Cloning \"$repoUrl\" as $repoName...", true
            )

            try {
                launch(CommonPool) {
                    Git.cloneRepository()
                        .setURI(repoUrl)
                        .setDirectory(rootFile)
                        .setBare(true)
                        .setCloneAllBranches(true)
                        .setProgressMonitor(object : EmptyProgressMonitor() {
                            override fun beginTask(title: String, totalWork: Int) {
                                launch(UI) {
                                    dialog.setMessage(title)
                                }
                            }
                        })
                        .setTransportConfigCallback(TransportCallback(this@AddRepositoryActivity))
                        .call()
                        .close()

                    val repository = GitLogRepository(0, repoName, repoUrl, rootFile.absolutePath)
                    repositoryListViewModel.addRepository(repository)
                }.join()

                dialog.dismiss()

                setResult(Activity.RESULT_OK)
                finish()
            } catch (error: InvalidRemoteException) {
                rootFile.deleteRecursively()

                repositoryUrlInputView.error = "Incorrect URL"
                repositoryUrlInputView.requestFocus()
            } catch (error: TransportException) {
                rootFile.deleteRecursively()

                AlertDialog.Builder(this@AddRepositoryActivity)
                    .setTitle("Failed cloning")
                    .setMessage(error.message)
                    .show()
            } finally {
                dialog.dismiss()
            }
        }
    }

    override fun getSupportParentActivityIntent(): Intent =
        RepositoryListActivityStarter.getIntent(this)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
}
