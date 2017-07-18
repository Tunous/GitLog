package me.thanel.gitlog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_repository_list.*
import java.io.File

class RepositoryListActivity : AppCompatActivity() {

    private val adapter = RepositoryListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository_list)

        repositoryRecyclerView.adapter = adapter
        repositoryRecyclerView.layoutManager = LinearLayoutManager(this)

        addRepositoryButton.setOnClickListener {
            val intent = AddRepositoryActivity.newIntent(this)
            startActivityForResult(intent, REQUEST_CLONE_REPOSITORY)
        }

        val rootFile = File(filesDir, REPOSITORIES_DIRECTORY)
        if (!rootFile.exists()) {
            rootFile.mkdir()
        }

        val repositoryDirectories = rootFile.listFiles { dir, _ ->
            dir.isDirectory
        }

        for (dir in repositoryDirectories) {
            adapter.add(Repository(dir.name))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CLONE_REPOSITORY) {
            if (resultCode == Activity.RESULT_OK) {
                val newRepository = data!!.getParcelableExtra<Repository>(EXTRA_REPOSITORY)
                adapter.add(newRepository)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private const val REPOSITORIES_DIRECTORY = "repos"

        const val REQUEST_CLONE_REPOSITORY = 1
        const val EXTRA_REPOSITORY = "extra.repository"
    }
}
