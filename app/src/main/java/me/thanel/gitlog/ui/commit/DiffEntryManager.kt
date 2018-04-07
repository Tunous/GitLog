package me.thanel.gitlog.ui.commit

import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.lib.AbbreviatedObjectId
import org.eclipse.jgit.lib.Repository
import java.io.ByteArrayOutputStream

class DiffEntryManager(private val gitRepository: Repository) {
    private val outputStream = ByteArrayOutputStream()
    private val diffFormatter = DiffFormatter(outputStream).apply {
        setRepository(gitRepository)
    }

    fun loadDiffEntries(commitSha: String): List<DiffEntry> {
        val previousCommit = gitRepository.resolve("$commitSha^")
        val currentCommit = gitRepository.resolve(commitSha)
        return diffFormatter.scan(previousCommit, currentCommit)
    }

    fun loadFormattedDiffEntries(commitSha: String): List<FormattedDiffEntry> {
        val diffEntries = loadDiffEntries(commitSha)
        return diffEntries.map { FormattedDiffEntry(it, loadDiffPatch(it)) }
    }

    fun loadDiffEntry(commitSha: String, diffId: AbbreviatedObjectId): DiffEntry? {
        val diffEntries = loadDiffEntries(commitSha)
        return diffEntries.find { it.newId == diffId }
    }

    fun loadFormattedDiffEntry(
        commitSha: String,
        diffId: AbbreviatedObjectId
    ): FormattedDiffEntry? {
        return loadDiffEntry(commitSha, diffId)?.let { diffEntry ->
            FormattedDiffEntry(diffEntry, loadDiffPatch(diffEntry))
        }
    }

    fun loadDiffPatch(diffEntry: DiffEntry): String {
        outputStream.reset()
        diffFormatter.format(diffEntry)
        diffFormatter.flush()
        return outputStream.toString("UTF-8")
    }
}
