package me.thanel.gitlog.utils

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Inflate a new view hierarchy from the specified xml resource.
 *
 * @param layoutResId ID for an XML layout resource to load (e.g., R.layout.main_page)
 * @param attachToRoot Whether the inflated hierarchy should be attached to the root parameter?
 * If `false`, root is only used to create the correct subclass of `LayoutParams` for the root view
 * in the XML.
 *
 * @return The root [View] of the inflated hierarchy. If [attachToRoot] is `true`, this is root;
 * otherwise it is the root of the inflated XML file.
 */
fun ViewGroup.inflate(@LayoutRes layoutResId: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutResId, this, attachToRoot)

/**
 * Run an operation with a progress dialog. It will be shown before the operation completes and
 * will be hidden when execution finishes.
 *
 * @param title The title of the progress dialog.
 * @param message The body message of the progress dialog.
 * @param indeterminate Whether the progress dialog should be indeterminate.
 * @param body The operation to perform.
 */
fun Context.withProgressDialog(title: String, message: String, indeterminate: Boolean = false,
        body: () -> Unit) {
    val dialog = ProgressDialog.show(this, title, message, indeterminate)
    try {
        body()
    } finally {
        dialog.dismiss()
    }
}

/**
 * Create a new instance of fragment with type [T] and initialize its arguments using the provided
 * [initializer].
 */
fun <T : Fragment> T.withArguments(initializer: Bundle.() -> Unit): T {
    arguments = Bundle().apply(initializer)
    return this
}

/**
 * Create a new intent for an activity of the specified type [T].
 */
inline fun <reified T : AppCompatActivity> Context.createIntent(): Intent =
        Intent(this, T::class.java)

/**
 * Create a new intent for an activity of the specified type [T] and initialize it with the
 * specified [initializer].
 */
inline fun <reified T : AppCompatActivity> Context.createIntent(
        initializer: Intent.() -> Unit): Intent = createIntent<T>().apply(initializer)
