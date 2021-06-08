package eu.kevin.core.networking

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

abstract class BaseCredentialStore(
    context: Context,
    storeName: String
) {
    private val securePreferences: SharedPreferences
    private val cache = HashMap<String, String?>()

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

    fun get(key: String) : String? {
        synchronized(this) {
            if (cache.containsKey(key)) {
                return cache[key]
            }

            val data = securePreferences.getString(key, null)
            cache[key] = data
            return data
        }
    }

    fun put(key: String, value: String?) {
        synchronized(this) {
            cache[key] = value
            with(securePreferences.edit()) {
                putString(key, value)
                apply()
            }
        }
    }

    fun remove(key: String) {
        synchronized(this) {
            cache.remove(key)
            with(securePreferences.edit()) {
                remove(key)
                apply()
            }
        }
    }

    fun clear() {
        synchronized(this) {
            cache.clear()
            with(securePreferences.edit()) {
                clear()
                apply()
            }
        }
    }
}