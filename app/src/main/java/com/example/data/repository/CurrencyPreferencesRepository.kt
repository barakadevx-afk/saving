package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.AppCurrency
import com.example.data.model.Language
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class CurrencyPreferencesRepository(private val context: Context) {

    private val CURRENCY_KEY = stringPreferencesKey("selected_currency_code")
    private val DARK_MODE_KEY = booleanPreferencesKey("is_dark_mode")
    private val LANGUAGE_KEY = stringPreferencesKey("selected_language_code")

    val selectedCurrencyFlow: Flow<AppCurrency> = context.dataStore.data.map { preferences ->
        val code = preferences[CURRENCY_KEY] ?: AppCurrency.RWF.code
        try {
            AppCurrency.valueOf(code)
        } catch (_: Exception) {
            AppCurrency.RWF
        }
    }

    val isDarkModeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    val selectedLanguageFlow: Flow<Language> = context.dataStore.data.map { preferences ->
        val code = preferences[LANGUAGE_KEY] ?: Language.EN.name
        try {
            Language.valueOf(code)
        } catch (_: Exception) {
            Language.EN
        }
    }

    suspend fun setCurrency(currency: AppCurrency) {
        context.dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currency.name
        }
    }

    suspend fun setDarkMode(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = isDark
        }
    }

    suspend fun setLanguage(language: Language) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language.name
        }
    }
}
