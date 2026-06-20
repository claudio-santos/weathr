package io.github.claudio_santos.weathr.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    fun applyLocale(context: Context, lang: String) {
        val locale = when (lang) {
            "pt" -> Locale("pt", "PT")
            else -> Locale.ENGLISH
        }
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
