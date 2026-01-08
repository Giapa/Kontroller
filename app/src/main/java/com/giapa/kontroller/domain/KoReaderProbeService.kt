package com.giapa.kontroller.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

/** Lightweight check for an HTTP service responding on port 8080 (does NOT validate KOReader). */
interface Port8080ProbeService {
    suspend fun isHttp8080Open(ip: String): Boolean
}

class HttpUrlConnectionPort8080ProbeService : Port8080ProbeService {

    override suspend fun isHttp8080Open(ip: String): Boolean = withContext(Dispatchers.IO) {
        val url = URL("http://$ip:8080/")
        val conn = (url.openConnection() as HttpURLConnection).apply {
            // HEAD is cheaper than GET; some servers might not support it, so we fall back.
            requestMethod = "HEAD"
            connectTimeout = 400
            readTimeout = 400
            instanceFollowRedirects = false
        }

        try {
            val code = runCatching { conn.responseCode }.getOrNull() ?: return@withContext false
            // Treat any HTTP response as "open"; KOReader validation happens on connect/tap.
            code in 100..599
        } catch (_: Exception) {
            false
        } finally {
            conn.disconnect()
        }
    }
}

/** Full KOReader validation: HTTP 200 and body contains "KOReader". */
interface KoReaderProbeService {
    suspend fun probe(ip: String): Result<Unit>
}

class HttpUrlConnectionKoReaderProbeService : KoReaderProbeService {

    override suspend fun probe(ip: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val url = URL("http://$ip:8080/")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 1_000
                readTimeout = 1_000
                instanceFollowRedirects = true
            }

            try {
                val code = conn.responseCode
                if (code != 200) {
                    throw IllegalStateException("Connection failed (HTTP $code)")
                }

                // Avoid keeping huge responses in memory; KOReader greeting is typically early.
                val body = conn.inputStream.bufferedReader().use { reader ->
                    buildString {
                        var lineCount = 0
                        while (lineCount < 50) {
                            val line = reader.readLine() ?: break
                            append(line)
                            append('\n')
                            if (contains("KOReader")) break
                            lineCount++
                        }
                    }
                }

                if (!body.contains("KOReader")) {
                    throw IllegalStateException("Connected, but KOReader not detected")
                }
            } finally {
                conn.disconnect()
            }
        }
    }
}
