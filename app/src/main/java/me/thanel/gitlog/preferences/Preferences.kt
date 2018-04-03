package me.thanel.gitlog.preferences

import com.chibatching.kotpref.KotprefModel

object Preferences : KotprefModel() {
    var showLineNumbers by booleanPref(default = true)
    var showGraph by booleanPref(default = true)
}
