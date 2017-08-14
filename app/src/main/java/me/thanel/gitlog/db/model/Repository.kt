package me.thanel.gitlog.db.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable
import org.eclipse.jgit.api.Git
import java.io.File

@Entity(tableName = "repositories")
data class Repository(
        @PrimaryKey(autoGenerate = true) val id: Int,
        val name: String,
        val url: String,
        val path: String
) : Parcelable {
    val git: Git get() = Git.open(File(path))

    constructor(parcel: Parcel) : this(
            id = parcel.readInt(),
            name = parcel.readString(),
            url = parcel.readString(),
            path = parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(url)
        parcel.writeString(path)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<Repository> {
        override fun createFromParcel(parcel: Parcel): Repository {
            return Repository(parcel)
        }

        override fun newArray(size: Int): Array<Repository?> {
            return arrayOfNulls(size)
        }
    }
}
