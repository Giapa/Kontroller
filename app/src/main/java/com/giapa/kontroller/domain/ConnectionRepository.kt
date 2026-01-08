package com.giapa.kontroller.domain

interface ConnectionRepository {
    suspend fun connect(endpoint: String): Result<Unit>
    suspend fun scan(endpoint: String): Result<Unit>

    /** Scan the local network for devices responding on http://<ip>:8080/ (best-effort). */
    suspend fun scanNetworkFor8080(): Result<List<String>>
}

class DefaultConnectionRepository(
    private val probeService: KoReaderProbeService = HttpUrlConnectionKoReaderProbeService(),
    private val portProbeService: Port8080ProbeService = HttpUrlConnectionPort8080ProbeService(),
) : ConnectionRepository {
    override suspend fun connect(endpoint: String): Result<Unit> {
        // For now, same behavior as scan: we only consider ourselves "connected" if KOReader is detected.
        return probeService.probe(endpoint)
    }

    override suspend fun scan(endpoint: String): Result<Unit> {
        return probeService.probe(endpoint)
    }

    override suspend fun scanNetworkFor8080(): Result<List<String>> {
        return NetworkScanner(portProbeService).scanLocal24Subnet()
    }
}

class FakeConnectionRepository : ConnectionRepository {
    override suspend fun connect(endpoint: String): Result<Unit> {
        if (endpoint.isBlank()) return Result.failure(IllegalArgumentException("Endpoint is blank"))
        return Result.success(Unit)
    }

    override suspend fun scan(endpoint: String): Result<Unit> = connect(endpoint)

    override suspend fun scanNetworkFor8080(): Result<List<String>> = Result.success(emptyList())
}
