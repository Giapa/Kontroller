package com.giapa.kontroller.domain

interface ConnectionRepository {
    suspend fun connect(endpoint: String): Result<Unit>
    suspend fun scan(endpoint: String): Result<Unit>
}

class DefaultConnectionRepository(
    private val probeService: KoReaderProbeService = HttpUrlConnectionKoReaderProbeService(),
) : ConnectionRepository {
    override suspend fun connect(endpoint: String): Result<Unit> {
        // For now, same behavior as scan: we only consider ourselves "connected" if KOReader is detected.
        return probeService.probe(endpoint)
    }

    override suspend fun scan(endpoint: String): Result<Unit> {
        return probeService.probe(endpoint)
    }
}

class FakeConnectionRepository : ConnectionRepository {
    override suspend fun connect(endpoint: String): Result<Unit> {
        if (endpoint.isBlank()) return Result.failure(IllegalArgumentException("Endpoint is blank"))
        return Result.success(Unit)
    }

    override suspend fun scan(endpoint: String): Result<Unit> = connect(endpoint)
}
