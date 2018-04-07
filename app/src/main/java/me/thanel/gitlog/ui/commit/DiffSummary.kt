package me.thanel.gitlog.ui.commit

data class DiffSummary(val formattedDiffEntries: List<FormattedDiffEntry>) {
    private val diffs = formattedDiffEntries
        .flatMap { it.formattedPatch.split("\n") }
        .dropWhile { !it.startsWith("@@") }
    val numAdditions = diffs.count { it.startsWith("+") }
    val numDeletions = diffs.count { it.startsWith("-") }
}
