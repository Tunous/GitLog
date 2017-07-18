package me.thanel.gitlog

import android.support.annotation.LayoutRes
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
