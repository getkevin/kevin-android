package eu.kevin.demo.base

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal abstract class SecurePreferences(
    context: Context,
    storeName: String
) {
    protected val securePreferences: SharedPreferences

    init {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        securePreferences = EncryptedSharedPreferences.create(
            context,
            storeName,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    protected fun getKeyFlow(): Flow<String?> {
        return callbackFlow {
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { pref, prefKey ->
                trySend(prefKey)
            }
            securePreferences.registerOnSharedPreferenceChangeListener(listener)
            awaitClose {
                securePreferences.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }
    }

    protected inline fun <reified T> get(key: String): T? {
        val jsonString = securePreferences.getString(key, null)
        val data = if (jsonString != null) {
            Json.decodeFromString<T>(jsonString)
        } else {
            null
        }
        return data
    }

    protected inline fun <reified T> put(key: String, value: T) {
        with(securePreferences.edit()) {
            val jsonString = Json.encodeToString(value)
            this.putString(key, jsonString)
            apply()
        }
    }

    protected fun getString(key: String): String? {
        return securePreferences.getString(key, null)
    }

    protected fun putString(key: String, value: String?) {
        with(securePreferences.edit()) {
            this.putString(key, value)
            apply()
        }
    }

    protected fun getFloat(key: String): Float {
        return securePreferences.getFloat(key, 0f)
    }

    protected fun putFloat(key: String, value: Float) {
        with(securePreferences.edit()) {
            this.putFloat(key, value)
            apply()
        }
    }

    protected fun getInt(key: String): Int {
        return securePreferences.getInt(key, 0)
    }

    protected fun putInt(key: String, value: Int) {
        with(securePreferences.edit()) {
            this.putInt(key, value)
            apply()
        }
    }

    protected fun getLong(key: String): Long {
        return securePreferences.getLong(key, 0L)
    }

    protected fun putLong(key: String, value: Long) {
        with(securePreferences.edit()) {
            this.putLong(key, value)
            apply()
        }
    }

    protected fun getBoolean(key: String): Boolean {
        return securePreferences.getBoolean(key, false)
    }

    protected fun putBoolean(key: String, value: Boolean) {
        with(securePreferences.edit()) {
            this.putBoolean(key, value)
            apply()
        }
    }

    protected fun contains(key: String): Boolean {
        return securePreferences.contains(key)
    }

    protected fun remove(key: String) {
        with(securePreferences.edit()) {
            remove(key)
            apply()
        }
    }

    protected fun clear() {
        with(securePreferences.edit()) {
            clear()
            apply()
        }
    }
}