package me.thanel.gitlog.di

import me.thanel.gitlog.db.GitLogDatabase
import me.thanel.gitlog.ui.commit.CommitViewModel
import me.thanel.gitlog.ui.repository.RepositoryViewModel
import me.thanel.gitlog.ui.repository.file.GitFileViewModel
import me.thanel.gitlog.ui.repository.filelist.GitFileListViewModel
import me.thanel.gitlog.ui.repositorylist.RepositoryListViewModel
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.applicationContext

val repositoryModule = applicationContext {
    viewModel { RepositoryListViewModel(get()) }
    viewModel { params ->
        RepositoryViewModel(get(), params[RepositoryViewModel.PARAM_REPOSITORY_ID])
    }
    viewModel { params -> GitFileListViewModel(params[GitFileListViewModel.PARAM_REF_NAME]) }
    viewModel { params ->
        GitFileViewModel(
            get(),
            params[GitFileViewModel.PARAM_REPOSITORY_ID],
            params[GitFileViewModel.PARAM_REF_NAME],
            params[GitFileViewModel.PARAM_FILE_PATH]
        )
    }
    viewModel { params ->
        CommitViewModel(
            get(),
            params[CommitViewModel.PARAM_REPOSITORY_ID],
            params[CommitViewModel.PARAM_COMMIT_SHA]
        )
    }

    bean { GitLogDatabase.buildDatabase(androidApplication()) }
    bean { get<GitLogDatabase>().repositoryDao() }
}
