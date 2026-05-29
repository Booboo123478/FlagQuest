package com.flagquest.app.utils

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object LanguageManager {

    val supportedLanguages = listOf(
        Language("en", "English", "🇬🇧"),
        Language("fr", "Français", "🇫🇷"),
        Language("es", "Español", "🇪🇸")
    )

    fun setLanguage(code: String) {
        val localeList = LocaleListCompat.forLanguageTags(code)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    fun getCurrentLanguageCode(): String {
        val locales = AppCompatDelegate.getApplicationLocales()
        return if (locales.isEmpty) "en" else locales[0]?.language ?: "en"
    }
}

data class Language(val code: String, val name: String, val flag: String)
