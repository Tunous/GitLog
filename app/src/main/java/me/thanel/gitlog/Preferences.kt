package me.thanel.gitlog

import com.chibatching.kotpref.KotprefModel

object Preferences : KotprefModel() {
    var showLineNumbers by booleanPref(default = true)
}