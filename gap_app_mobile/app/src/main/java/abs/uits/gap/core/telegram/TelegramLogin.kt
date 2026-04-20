package abs.uits.gap.core.telegram

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

/**
 * Implementation of Telegram Login OAuth flow.
 * Adapted from official Telegram Login SDK source.
 */
class TelegramLogin private constructor(
    private val botId: String,
    private val onLoginSuccess: (LoginData) -> Unit,
    private val onLoginError: (LoginError) -> Unit
) {

    class Builder(private val botId: String) {
        private var onLoginSuccess: (LoginData) -> Unit = {}
        private var onLoginError: (LoginError) -> Unit = {}

        fun setOnLoginSuccess(listener: (LoginData) -> Unit) = apply { this.onLoginSuccess = listener }
        fun setOnLoginError(listener: (LoginError) -> Unit) = apply { this.onLoginError = listener }

        fun build() = TelegramLogin(botId, onLoginSuccess, onLoginError)
    }

    companion object {
        private const val AUTH_URL = "https://oauth.telegram.org/auth"
    }

    /**
     * Start the login process.
     */
    fun login(activity: Activity) {
        val uri = Uri.parse(AUTH_URL).buildUpon()
            .appendQueryParameter("bot_id", botId)
            .appendQueryParameter("origin", "https://gapuz.duckdns.org")
            .appendQueryParameter("embed", "1")
            .appendQueryParameter("request_access", "write")
            .appendQueryParameter("return_to", "abs.uits.gap://telegram-auth")
            .build()

        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("org.telegram.messenger")

        if (intent.resolveActivity(activity.packageManager) != null) {
            activity.startActivity(intent)
        } else {
            // Fallback to custom tabs if Telegram app is not installed
            val customTabsIntent = CustomTabsIntent.Builder().build()
            customTabsIntent.launchUrl(activity, uri)
        }
    }

    /**
     * Process the login redirect intent.
     */
    fun handleIntent(intent: Intent?) {
        val data = intent?.data
        if (data != null && data.scheme == "abs.uits.gap" && data.host == "telegram-auth") {
            val params = mutableMapOf<String, Any>()
            data.queryParameterNames.forEach { name ->
                val value = data.getQueryParameter(name)
                if (value != null) params[name] = value
            }
            if (params.containsKey("hash")) {
                onLoginSuccess(LoginData(params["hash"] as String))
            } else {
                val error = data.getQueryParameter("error") ?: "Unknown error"
                onLoginError(LoginError(error))
            }
        }
    }
}
