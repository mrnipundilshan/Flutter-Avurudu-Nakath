package com.aurudu.app.data

import android.content.Context
import androidx.core.content.edit

object LanguagePreference {
    private const val PREFS_NAME = "language_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    fun get(context: Context): Language {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val stored = prefs.getString(KEY_LANGUAGE, Language.SINHALA.name)
        return runCatching { Language.valueOf(stored ?: Language.SINHALA.name) }
            .getOrDefault(Language.SINHALA)
    }

    fun set(context: Context, language: Language) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit {
                putString(KEY_LANGUAGE, language.name)
            }
    }
}
