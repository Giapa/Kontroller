package com.giapa.kontroller.domain

interface ConnectionRepository {
    suspend fun connect(endpoint: String): Result<Unit>
}

class FakeConnectionRepository : ConnectionRepository {
    override suspend fun connect(endpoint: String): Result<Unit> {
        if (endpoint.isBlank()) return Result.failure(IllegalArgumentException("Endpoint is blank"))
        return Result.success(Unit)
    }
}

