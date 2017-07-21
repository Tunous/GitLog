package me.thanel.gitlog

import android.os.Parcel
import android.os.Parcelable
import org.eclipse.jgit.revwalk.RevCommit

data class Commit(
        val authorName: String,
        val commitTime: Int,
        val shortMessage: String,
        val fullMessage: String,
        val sha: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
            authorName = parcel.readString(),
            commitTime = parcel.readInt(),
            shortMessage = parcel.readString(),
            fullMessage = parcel.readString(),
            sha = parcel.readString()
    )

    constructor(commit: RevCommit) : this(
            authorName = commit.authorIdent.name,
            commitTime = commit.commitTime,
            shortMessage = commit.shortMessage,
            fullMessage = commit.fullMessage,
            sha = commit.name
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(authorName)
        parcel.writeInt(commitTime)
        parcel.writeString(shortMessage)
        parcel.writeString(fullMessage)
        parcel.writeString(sha)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Commit> {
        override fun createFromParcel(parcel: Parcel): Commit {
            return Commit(parcel)
        }

        override fun newArray(size: Int): Array<Commit?> {
            return arrayOfNulls(size)
        }
    }
}

val Commit.shortSha get() = sha.substring(0, 7)