package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Call the Gemini API to generate text based on the provided prompt.
     */
    suspend fun generateContent(prompt: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is not configured or is placeholder. Using simulated response.")
            return@withContext getSimulatedResponse(prompt)
        }

        val jsonMedia = "application/json; charset=utf-8".toMediaType()
        val escapedPrompt = escapeJson(prompt)
        val requestBodyString = "{\"contents\":[{\"parts\":[{\"text\":$escapedPrompt}]}]}"

        val request = Request.Builder()
            .url("$BASE_URL?key=$apiKey")
            .post(requestBodyString.toRequestBody(jsonMedia))
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "Gemini API failure: code=${response.code}, body=$errBody")
                    return@withContext "I experienced an issue communicating with my AI brain. Please check your network connection."
                }

                val bodyString = response.body?.string() ?: ""
                val textResponse = parseGeminiTextResponse(bodyString)
                if (textResponse.isBlank()) {
                    return@withContext "My thought process didn't produce any output. Ask me something else!"
                }
                textResponse
            }
        } catch (e: IOException) {
            Log.e(TAG, "Gemini network exception", e)
            "A network timeout occurred while retrieving AI response."
        } catch (e: Exception) {
            Log.e(TAG, "Gemini general exception", e)
            "An error occurred while generating response: ${e.localizedMessage}"
        }
    }

    /**
     * Safely escapes strings for raw JSON payloads without relying on external serialization engines.
     */
    private fun escapeJson(text: String): String {
        val builder = StringBuilder()
        for (char in text) {
            when (char) {
                '\"' -> builder.append("\\\"")
                '\\' -> builder.append("\\\\")
                '\b' -> builder.append("\\b")
                '\n' -> builder.append("\\n")
                '\r' -> builder.append("\\r")
                '\t' -> builder.append("\\t")
                else -> {
                    if (char < ' ') {
                        builder.append(String.format("\\u%04x", char.code))
                    } else {
                        builder.append(char)
                    }
                }
            }
        }
        return "\"$builder\""
    }

    /**
     * Light, robust JSON text extractor that parses the model's text answer.
     */
    private fun parseGeminiTextResponse(json: String): String {
        val textKey = "\"text\":"
        var startIndex = json.indexOf(textKey)
        if (startIndex == -1) return ""

        startIndex = json.indexOf("\"", startIndex + textKey.length)
        if (startIndex == -1) return ""

        val contentBuilder = StringBuilder()
        var i = startIndex + 1
        while (i < json.length) {
            val char = json[i]
            if (char == '\\') {
                if (i + 1 < json.length) {
                    val nextChar = json[i + 1]
                    when (nextChar) {
                        '\"' -> contentBuilder.append('\"')
                        '\\' -> contentBuilder.append('\\')
                        'n' -> contentBuilder.append('\n')
                        'r' -> contentBuilder.append('\r')
                        't' -> contentBuilder.append('\t')
                        else -> contentBuilder.append(nextChar)
                    }
                    i += 2
                } else {
                    i++
                }
            } else if (char == '\"') {
                break
            } else {
                contentBuilder.append(char)
                i++
            }
        }
        return contentBuilder.toString().trim()
    }

    /**
     * Elegant offline simulation as a fallback if the API key is not configured.
     */
    private fun getSimulatedResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("translate") -> {
                "📍 [Luna AI Translation Feature]: \"Hola! ¿Cómo estás? Me encanta usar Lumexa para hacer videollamadas con amigos de todo el mundo. ¡Hablemos pronto!\" (Translated to Spanish)"
            }
            lower.contains("summarize") || lower.contains("summary") -> {
                "📝 [Luna AI Summarization]: Lumexa facilitates high-fidelity social audio/video calls, and private text chats. Users can purchase digital coins to tip content creators, join live voice rooms hosted by verified partners, and upgrade to premium VIP profiles."
            }
            lower.contains("optimize") || lower.contains("bio") -> {
                "✨ [Luna AI Profile Optimization Suggestion]:\n\"Hey there! I am a passionate tech developer & photographer on Lumexa. Let's exchange ideas in the Voice Rooms or jump on a quick call! 🚀📸\""
            }
            lower.contains("spam") || lower.contains("moderate") -> {
                "🛡️ [Luna AI Security Check]: Content flagged as safe. Threat index: 0%. Lumexa maintains real-time user-safety standard protocols."
            }
            else -> {
                "Hello from Luna AI! 🧠 Lumexa has successfully integrated the Gemini-3.5-Flash model! Ask me to translate chats, summarize long voice room logs, optimize your profile bio, or detect spam posts in real-time."
            }
        }
    }
}
