package com.giapa.kontroller.domain

import com.giapa.kontroller.util.LocalIp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Scans the local /24 subnet (x.y.z.1..254) to find hosts responding on HTTP port 8080.
 */
class NetworkScanner(
    private val portProbe: Port8080ProbeService,
) {

    suspend fun scanLocal24Subnet(
        maxConcurrency: Int = 96,
        perHostTimeoutMs: Long = 600,
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        runCatching {
            val localIp = LocalIp.getLocalIpv4Address()
                ?: throw IllegalStateException("No local IP address found (are you on Wi‑Fi?)")
            val prefix = LocalIp.to24SubnetPrefix(localIp)
                ?: throw IllegalStateException("Unsupported local IP: $localIp")

            val hosts = (1..254).map { host -> "$prefix.$host" }
            val semaphore = Semaphore(maxConcurrency)

            coroutineScope {
                hosts.map { ip ->
                    async {
                        semaphore.withPermit {
                            val open = withTimeoutOrNull(perHostTimeoutMs) {
                                portProbe.isHttp8080Open(ip)
                            } ?: false
                            if (open) ip else null
                        }
                    }
                }.awaitAll().filterNotNull().sorted()
            }
        }
    }
}
