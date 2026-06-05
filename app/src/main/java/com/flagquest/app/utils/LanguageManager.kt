package com.flagquest.app.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import java.util.Locale

object LanguageManager {

    val supportedLanguages = listOf(
        Language("en", "English", "🇬🇧"),
        Language("fr", "Français", "🇫🇷"),
        Language("es", "Español", "🇪🇸")
    )

    fun setLanguage(activity: Activity, code: String) {
        saveLanguageCode(activity, code)
        // Recrée l'Activity pour appliquer la nouvelle langue
        val intent = activity.intent
        activity.finish()
        activity.startActivity(intent)
    }

    fun applyLanguage(context: Context): Context {
        val code = getSavedLanguageCode(context)
        val locale = Locale(code)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    fun getCurrentLanguageCode(context: Context): String = getSavedLanguageCode(context)

    private fun saveLanguageCode(context: Context, code: String) {
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .edit().putString("language", code).apply()
    }

    private fun getSavedLanguageCode(context: Context): String =
        context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            .getString("language", "en") ?: "en"
}

data class Language(val code: String, val name: String, val flag: String)
