package me.thanel.gitlog.utils

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.AttrRes
import android.support.annotation.ColorInt
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

/**
 * Inflate a new view hierarchy from the specified xml resource.
 *
 * @param layoutResId ID for an XML layout resource to load (e.g., R.layout.main_page)
 * @param attachToRoot Whether the inflated hierarchy should be attached to the root parameter?
 * If `false`, root is only used to create the correct subclass of `LayoutParams` for the root view
 * in the XML.
 * @return The root [View] of the inflated hierarchy. If [attachToRoot] is `true`, this is root;
 * otherwise it is the root of the inflated XML file.
 */
fun ViewGroup.inflate(@LayoutRes layoutResId: Int, attachToRoot: Boolean = false): View =
        LayoutInflater.from(context).inflate(layoutResId, this, attachToRoot)

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

/**
 * Returns displayable styled text from the provided HTML string.
 */
@Suppress("DEPRECATION")
fun String.fromHtml(): Spanned {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
    } else {
        Html.fromHtml(this)
    }
}

class StyleableTag(name: String, val replacement: String, vararg val spans: Any) {
    val name = "[$name]"
}

/**
 * Format all [tags] inside of this string with the specified replacement strings and apply any
 * provided spans on the replaced text.
 */
fun String.formatTags(vararg tags: StyleableTag): SpannableStringBuilder {
    val builder = SpannableStringBuilder()
    var pos = 0

    for (tag in tags) {
        val start = indexOf(tag.name, pos)
        val end = start + tag.name.length
        if (start >= 0 && end >= 0) {
            builder.append(substring(pos, start))
            builder.append(tag.replacement)

            val spanEnd = builder.length
            val spanStart = spanEnd - tag.replacement.length
            for (span in tag.spans) {
                builder.setSpan(span, spanStart, spanEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            pos = end
        }
    }
    builder.append(substring(pos))
    return builder
}

inline fun <T> LiveData<T>.observe(owner: LifecycleOwner, crossinline observer: (T?) -> Unit) {
    observe(owner, Observer { observer(it) })
}

/**
 * Applies the given function on CommonPool background thread to each value emitted by this
 * LiveData and returns LiveData, which emits resulting values.
 *
 * The given function [func] will be executed on the background thread.
 *
 * @param func The function to apply.
 * @param X The type of source data.
 * @param Y The type of resulting data.
 * @return A LiveData which emits resulting values.
 * @see Transformations.map
 */
fun <X, Y> LiveData<X>.mapBg(func: (X) -> Y): LiveData<Y> = Transformations.switchMap(this) {
    BackgroundLoadLiveData(it, func)
}

/**
 * Live data which immediately applies the given function on the provided data in
 * a CommonPool background thread and sets its results as a value.
 *
 * @param input The input data to map.
 * @param func The function to apply.
 */
class BackgroundLoadLiveData<X, Y>(input: X, func: (X) -> Y) : MutableLiveData<Y>() {
    init {
        launch(CommonPool) {
            postValue(func(input))
        }
    }
}

inline fun <reified T : ViewModel> getViewModel(activity: FragmentActivity): T {
    return ViewModelProviders.of(activity).get(T::class.java)
}

inline fun <reified T : ViewModel> getViewModel(activity: FragmentActivity,
        crossinline onCreateFactory: () -> T): T {
    val factory = object : ViewModelProvider.Factory {
        override fun <X : ViewModel> create(modelClass: Class<X>): X {
            @Suppress("UNCHECKED_CAST")
            return onCreateFactory() as X
        }
    }
    val provider = ViewModelProviders.of(activity, factory)
    return provider.get(T::class.java)
}

@ColorInt
fun Context.resolveColor(@AttrRes attResId: Int): Int {
    val a = obtainStyledAttributes(intArrayOf(attResId))
    val color = a.getColor(0, 0)
    a.recycle()
    return color
}

@Suppress("unused")
fun showNotImplementedToast(context: Context) {
    Toast.makeText(context, "Not implemented", Toast.LENGTH_SHORT).show()
}

const val SHORT_SHA_LENGTH = 7
