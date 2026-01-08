package com.giapa.kontroller.domain

interface ControllerClient {
    suspend fun sendBack(): Result<Unit>
    suspend fun sendForward(): Result<Unit>
    suspend fun micPress(): Result<Unit>
}

class FakeControllerClient : ControllerClient {
    override suspend fun sendBack(): Result<Unit> = Result.success(Unit)
    override suspend fun sendForward(): Result<Unit> = Result.success(Unit)
    override suspend fun micPress(): Result<Unit> = Result.success(Unit)
}

