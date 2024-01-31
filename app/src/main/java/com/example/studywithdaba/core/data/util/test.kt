package com.example.studywithdaba.core.data.util

import android.util.Log
import com.example.studywithdaba.feature_note.NotesViewModel
import com.google.gson.JsonParser
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

fun main() {
    try {
        val client = OkHttpClient()
        val apiKey = "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"
        val url = "https://api.openai.com/v1/chat/completions"

        val requestBody = """
            {
                "model": "gpt-3.5-turbo",
                "messages": [
                {"role": "system", "content": "generate questions with answers for text in format: question;answer;question; ..."},
                {"role": "user", "content": "The history of Spain dates to contact between the pre-Roman peoples of the Mediterranean coast of the Iberian Peninsula made with the Greeks and Phoenicians. During Classical Antiquity, the peninsula was the site of multiple successive colonizations of Greeks, Carthaginians, and Romans. Native peoples of the peninsula, such as the Tartessos people, intermingled with the colonizers to create a uniquely Iberian culture"}
                ],
                "max_tokens": 200,
                "temperature": 0
            }
        """.trimIndent()

        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $apiKey")
            .post(requestBody.toRequestBody("application/json".toMediaTypeOrNull()))
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("error", "API Failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                if (body != null) {
                    Log.v("reponse", body)



                }
            }


        })
    } catch (e: Exception) {

    }

}
