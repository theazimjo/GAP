package abs.uits.gap.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys

class TokenStorage(context: Context) {
    private val sharedPreferences: SharedPreferences = createEncryptedSharedPreferences(context)

    fun saveToken(token: String) {
        sharedPreferences.edit().putString("auth_token", token).apply()
    }

    fun getToken(): String? {
        return try {
            sharedPreferences.getString("auth_token", null)
        } catch (e: Exception) {
            null
        }
    }

    fun clear() {
        sharedPreferences.edit().remove("auth_token").apply()
    }

    private fun createEncryptedSharedPreferences(context: Context): SharedPreferences {
        val fileName = "auth_prefs"
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        return try {
            EncryptedSharedPreferences.create(
                fileName,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            Log.e("TokenStorage", "Error creating EncryptedSharedPreferences, retrying with clear data", e)
            
            // Clear corrupted preferences
            try {
                context.getSharedPreferences(fileName, Context.MODE_PRIVATE).edit().clear().apply()
            } catch (ignored: Exception) {}

            // The keyset is usually stored in a file named: fileName + "_androidx_security_crypto_encrypted_prefs_key_set__"
            // Deleting it or clearing it can help if the keyset itself is corrupted.
            // However, a simpler way is to just use a different file name if it fails repeatedly, 
            // but here we just try one more time after clear.
            
            EncryptedSharedPreferences.create(
                fileName,
                masterKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }
}
