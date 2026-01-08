package com.giapa.kontroller.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

interface KoReaderProbeService {
    suspend fun probe(ip: String): Result<Unit>
}

class HttpUrlConnectionKoReaderProbeService : KoReaderProbeService {

    override suspend fun probe(ip: String): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val url = URL("http://$ip:8080/")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 2_000
                readTimeout = 2_000
                instanceFollowRedirects = true
            }

            try {
                val code = conn.responseCode
                if (code != 200) {
                    throw IllegalStateException("Connection failed (HTTP $code)")
                }

                val body = conn.inputStream.bufferedReader().use { it.readText() }
                if (!body.contains("KOReader")) {
                    throw IllegalStateException("Connected, but KOReader not detected")
                }
            } finally {
                conn.disconnect()
            }
        }
    }
}
