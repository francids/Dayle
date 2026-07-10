package fyi.dayle.data

import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets

class MissionService(private val baseUrl: String) {

    fun fetchMission(previousMissions: List<String>): String {
        val connection = (URL("$baseUrl/mission").openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 15_000
            readTimeout = 30_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
        }

        try {
            val body = JSONObject().apply {
                put("previousMissions", JSONArray(previousMissions))
            }.toString()

            OutputStreamWriter(connection.outputStream, StandardCharsets.UTF_8).use {
                it.write(body)
                it.flush()
            }

            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val responseText =
                stream?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() }.orEmpty()

            if (code !in 200..299) {
                throw MissionServiceException("HTTP $code: $responseText")
            }

            val parsed = JSONObject(responseText)
            val mission = parsed.optString("mission").trim()
            if (mission.isEmpty()) {
                throw MissionServiceException("Empty mission in response")
            }
            return mission
        } finally {
            connection.disconnect()
        }
    }
}

class MissionServiceException(message: String) : Exception(message)
