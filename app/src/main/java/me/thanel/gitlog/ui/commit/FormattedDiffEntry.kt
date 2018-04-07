package me.thanel.gitlog.ui.commit

import org.eclipse.jgit.diff.DiffEntry

data class FormattedDiffEntry(val diffEntry: DiffEntry, val formattedPatch: String)
