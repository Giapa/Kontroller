package com.giapa.kontroller.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class HttpControllerClient(
    private val session: ConnectionSession = ConnectionSession,
) : ControllerClient {

    override suspend fun sendBack(): Result<Unit> = sendGotoViewRel(-1)

    override suspend fun sendForward(): Result<Unit> = sendGotoViewRel(1)

    override suspend fun micPress(): Result<Unit> {
        return Result.success(Unit)
    }

    private suspend fun sendGotoViewRel(delta: Int): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val endpoint = session.endpoint?.trim().orEmpty()
            if (endpoint.isBlank()) {
                throw IllegalStateException("No endpoint selected")
            }

            val url = URL("http://$endpoint:8080/koreader/event/GotoViewRel/$delta")
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 1_000
                readTimeout = 1_000
                instanceFollowRedirects = false
            }

            try {
                val code = conn.responseCode
                if (code !in 200..299) {
                    throw IllegalStateException("Request failed (HTTP $code)")
                }
            } finally {
                conn.disconnect()
            }
        }
    }
}

