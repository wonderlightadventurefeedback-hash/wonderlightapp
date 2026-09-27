package com.example.data.remote

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class SupabaseUser(
    val id: String,
    val email: String,
    val fullName: String,
    val phone: String = "",
    val accessToken: String? = null
)

sealed class AuthResult {
    data class Success(val user: SupabaseUser, val message: String) : AuthResult()
    data class Error(val errorMessage: String) : AuthResult()
}

object SupabaseAuthManager {
    private const val TAG = "SupabaseAuthManager"
    
    // Supabase project credentials
    const val SUPABASE_URL = "https://elcahnyfaogvittfrxaa.supabase.co"
    const val SUPABASE_KEY = "sb_publishable_Yj7EQj1ZKy3ro5cEGt0r7A_EhUttYan"
    const val SERVICE_KEY = "sb_secret_AFiaNX8om4SMXprlDVQpUA_OIehjX1v"

    private const val PREF_NAME = "wonderlight_supabase_auth"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_EMAIL = "email"
    private const val KEY_FULL_NAME = "full_name"
    private const val KEY_PHONE = "phone"
    private const val KEY_TOKEN = "access_token"

    /**
     * Validates strong password requirement (8+ chars, at least 1 digit, at least 1 special char)
     */
    fun validatePassword(password: String): String? {
        if (password.length < 8) {
            return "Password must be at least 8 characters long."
        }
        if (!password.any { it.isDigit() }) {
            return "Password must contain at least one number."
        }
        val specialChars = "!@#\$%^&*()-_=+[]{}|;:'\",.<>?/`~"
        if (!password.any { it in specialChars }) {
            return "Password must contain at least one special character (@, #, $, %, etc.)."
        }
        return null
    }

    fun getSavedUser(context: Context): SupabaseUser? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val id = prefs.getString(KEY_USER_ID, null) ?: return null
        val email = prefs.getString(KEY_EMAIL, "") ?: ""
        val fullName = prefs.getString(KEY_FULL_NAME, "Traveller") ?: "Traveller"
        val phone = prefs.getString(KEY_PHONE, "") ?: ""
        val token = prefs.getString(KEY_TOKEN, null)
        return SupabaseUser(id = id, email = email, fullName = fullName, phone = phone, accessToken = token)
    }

    fun saveUserSession(context: Context, user: SupabaseUser) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_USER_ID, user.id)
            .putString(KEY_EMAIL, user.email)
            .putString(KEY_FULL_NAME, user.fullName)
            .putString(KEY_PHONE, user.phone)
            .putString(KEY_TOKEN, user.accessToken)
            .apply()
    }

    fun clearSession(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }

    private fun autoConfirmUserByEmail(email: String) {
        try {
            val url = URL("$SUPABASE_URL/auth/v1/admin/users")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", SERVICE_KEY)
                setRequestProperty("Authorization", "Bearer $SERVICE_KEY")
                connectTimeout = 4000
                readTimeout = 4000
            }
            if (conn.responseCode in 200..299) {
                val resp = conn.inputStream.bufferedReader().use(BufferedReader::readText)
                val json = JSONObject(resp)
                val users = json.optJSONArray("users")
                if (users != null) {
                    for (i in 0 until users.length()) {
                        val u = users.getJSONObject(i)
                        if (u.optString("email").equals(email.trim(), ignoreCase = true)) {
                            val uid = u.optString("id")
                            if (uid.isNotBlank()) {
                                val putUrl = URL("$SUPABASE_URL/auth/v1/admin/users/$uid")
                                val putConn = (putUrl.openConnection() as HttpURLConnection).apply {
                                    requestMethod = "PUT"
                                    setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                                    setRequestProperty("apikey", SERVICE_KEY)
                                    setRequestProperty("Authorization", "Bearer $SERVICE_KEY")
                                    doOutput = true
                                    connectTimeout = 4000
                                    readTimeout = 4000
                                }
                                val putBody = JSONObject().apply { put("email_confirm", true) }
                                OutputStreamWriter(putConn.outputStream).use { it.write(putBody.toString()); it.flush() }
                                putConn.responseCode
                                putConn.disconnect()
                            }
                            break
                        }
                    }
                }
            }
            conn.disconnect()
        } catch (e: Exception) {
            Log.w(TAG, "Auto-confirm failed: ${e.message}")
        }
    }

    /**
     * Signs in an existing user with Supabase Auth (GoTrue API)
     */
    suspend fun signIn(email: String, password: String, retryCount: Int = 0): AuthResult = withContext(Dispatchers.IO) {
        try {
            val url = URL("$SUPABASE_URL/auth/v1/token?grant_type=password")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.setRequestProperty("apikey", SUPABASE_KEY)
            conn.setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
            conn.connectTimeout = 6000
            conn.readTimeout = 6000
            conn.doOutput = true

            val reqBody = JSONObject().apply {
                put("email", email.trim())
                put("password", password)
            }

            OutputStreamWriter(conn.outputStream).use { it.write(reqBody.toString()); it.flush() }

            val responseCode = conn.responseCode
            val responseStream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
            val responseText = responseStream.bufferedReader().use(BufferedReader::readText)
            conn.disconnect()

            val json = JSONObject(responseText)
            if (responseCode in 200..299) {
                val token = json.optString("access_token", "")
                val userObj = json.optJSONObject("user")
                val userId = userObj?.optString("id", "") ?: ""
                val userEmail = userObj?.optString("email", email.trim()) ?: email.trim()
                val meta = userObj?.optJSONObject("user_metadata")
                val fullName = meta?.optString("full_name", userEmail.substringBefore("@").replaceFirstChar { it.uppercase() })
                    ?: userEmail.substringBefore("@")
                val phone = meta?.optString("phone", "") ?: ""

                val user = SupabaseUser(
                    id = userId,
                    email = userEmail,
                    fullName = fullName,
                    phone = phone,
                    accessToken = token
                )
                AuthResult.Success(user, "Welcome back, $fullName!")
            } else {
                val errMsg = json.optString("error_description", json.optString("msg", "Invalid email or password"))
                // Auto-confirm unconfirmed emails and retry once
                if (retryCount == 0 && (errMsg.contains("confirm", ignoreCase = true) || errMsg.contains("Email not confirmed", ignoreCase = true))) {
                    autoConfirmUserByEmail(email)
                    return@withContext signIn(email, password, retryCount = 1)
                }
                AuthResult.Error(errMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Supabase signIn network error", e)
            AuthResult.Error("Network error: ${e.localizedMessage ?: "Unable to connect to Supabase"}")
        }
    }

    /**
     * Registers a new user with Supabase Auth
     * Creates with email_confirm = true so sign in works immediately!
     */
    suspend fun signUp(email: String, password: String, fullName: String, phone: String = ""): AuthResult = withContext(Dispatchers.IO) {
        val passErr = validatePassword(password)
        if (passErr != null) {
            return@withContext AuthResult.Error(passErr)
        }

        try {
            // First attempt to create directly confirmed via Admin API
            var createdDirectly = false
            try {
                val adminUrl = URL("$SUPABASE_URL/auth/v1/admin/users")
                val adminConn = (adminUrl.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                    setRequestProperty("apikey", SERVICE_KEY)
                    setRequestProperty("Authorization", "Bearer $SERVICE_KEY")
                    connectTimeout = 5000
                    readTimeout = 5000
                    doOutput = true
                }
                val adminBody = JSONObject().apply {
                    put("email", email.trim())
                    put("password", password)
                    put("email_confirm", true)
                    put("user_metadata", JSONObject().apply {
                        put("full_name", fullName.trim())
                        put("phone", phone.trim())
                    })
                }
                OutputStreamWriter(adminConn.outputStream).use { it.write(adminBody.toString()); it.flush() }
                if (adminConn.responseCode in 200..299) {
                    createdDirectly = true
                }
                adminConn.disconnect()
            } catch (_: Exception) {}

            if (createdDirectly) {
                // Immediately sign in to get active access token
                return@withContext signIn(email.trim(), password)
            }

            // Fallback to standard signup endpoint
            val url = URL("$SUPABASE_URL/auth/v1/signup")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.setRequestProperty("apikey", SUPABASE_KEY)
            conn.setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
            conn.connectTimeout = 6000
            conn.readTimeout = 6000
            conn.doOutput = true

            val reqBody = JSONObject().apply {
                put("email", email.trim())
                put("password", password)
                put("data", JSONObject().apply {
                    put("full_name", fullName.trim())
                    put("phone", phone.trim())
                })
            }

            OutputStreamWriter(conn.outputStream).use { it.write(reqBody.toString()); it.flush() }

            val responseCode = conn.responseCode
            val responseStream = if (responseCode in 200..299) conn.inputStream else conn.errorStream
            val responseText = responseStream.bufferedReader().use(BufferedReader::readText)
            conn.disconnect()

            val json = JSONObject(responseText)
            if (responseCode in 200..299) {
                // Auto confirm newly created account
                autoConfirmUserByEmail(email.trim())
                // Sign in to receive session
                signIn(email.trim(), password)
            } else {
                val errMsg = json.optString("error_description", json.optString("msg", "Unable to sign up"))
                AuthResult.Error(errMsg)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Supabase signUp network error", e)
            AuthResult.Error("Network error: ${e.localizedMessage ?: "Unable to connect to Supabase"}")
        }
    }
}
