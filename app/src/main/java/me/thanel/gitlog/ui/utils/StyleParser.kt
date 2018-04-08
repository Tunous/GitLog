package me.thanel.gitlog.ui.utils

import android.content.Context
import android.support.annotation.ColorInt
import androidx.graphics.toColorInt
import com.pddstudio.highlightjs.models.Theme
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.InputStreamReader

class StyleParser(private val theme: Theme) {
    private val backgroundColorRegex = """\s*background:[^#]*(#[0-9a-f]{1,8})\b.*;""".toRegex()

    @ColorInt
    fun parseBackgroundColor(context: Context): Int? {
        val inputStream = getInputStreamFromAssets(context) ?: return null
        return parseBackgroundColor(inputStream)
    }

    private fun getFileName() = "styles/${theme.getName()}.css"

    private fun getInputStreamFromAssets(context: Context) = try {
        context.assets.open(getFileName())
    } catch (ex: FileNotFoundException) {
        null
    }

    @ColorInt
    private fun parseBackgroundColor(inputStream: InputStream): Int? {
        BufferedReader(InputStreamReader(inputStream)).useLines { lines ->
            var foundPrimaryBlock = false
            lines.forEach {
                if (!foundPrimaryBlock) {
                    // First find .hljs block which should contain background color rule
                    foundPrimaryBlock = it.startsWith(".hljs {")
                } else {
                    val matchResult = backgroundColorRegex.find(it)
                    if (matchResult != null) {
                        return try {
                            matchResult.groupValues[1].toColorInt()
                        } catch (ex: IllegalArgumentException) {
                            // The color has unknown format
                            null
                        }
                    }

                    // If reached closing tag then there is no background color definition...
                    if (it.startsWith('}')) {
                        return null
                    }
                }
            }
        }
        return null
    }
}
