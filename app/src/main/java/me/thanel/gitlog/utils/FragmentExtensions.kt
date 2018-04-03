package me.thanel.gitlog.utils

import android.os.Parcelable
import android.support.v4.app.Fragment
import java.io.Serializable

fun <T : Parcelable> Fragment.parcelableArg(name: String) = lazy {
    arguments!!.getParcelable<T>(name)!!
}

fun Fragment.intArg(name: String) = lazy {
    arguments!!.getInt(name)
}

fun Fragment.stringArg(name: String) = lazy {
    arguments!!.getString(name)!!
}

fun Fragment.stringArrayArg(name: String) = lazy {
    arguments!!.getStringArray(name)!!
}

fun <T : Serializable> Fragment.serializableArg(name: String) = lazy {
    @Suppress("UNCHECKED_CAST")
    arguments!!.getSerializable(name) as T
}
