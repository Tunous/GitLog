package me.thanel.gitlog.ui.diff

import activitystarter.Arg
import android.os.Bundle
import com.marcinmoskala.activitystarter.argExtra
import com.pddstudio.highlightjs.models.Language
import me.thanel.gitlog.ui.base.fragment.BaseWebViewerFragment
import me.thanel.gitlog.ui.commit.FormattedDiffEntry
import me.thanel.gitlog.ui.utils.observe
import org.eclipse.jgit.lib.AbbreviatedObjectId
import org.koin.android.architecture.ext.viewModel

class DiffViewerFragment : BaseWebViewerFragment() {
    @get:Arg
    val commitSha: String by argExtra()

    @get:Arg
    val repositoryId: Int by argExtra()

    @get:Arg
    val diffId: AbbreviatedObjectId by argExtra()

    private val viewModel by viewModel<DiffViewModel> {
        DiffViewModel.createParams(repositoryId, commitSha, diffId)
    }

    override val language: Language
        get() = Language.DIFF

    override val showLineNumbers: Boolean
        get() = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.formattedDiffEntry.observe(this, ::displayDiff)
    }

    private fun displayDiff(formattedDiffEntry: FormattedDiffEntry?) {
        if (formattedDiffEntry == null) {
            // TODO: Loading or error...
            return
        }

        setSource(formattedDiffEntry.formattedPatch)
    }
}
