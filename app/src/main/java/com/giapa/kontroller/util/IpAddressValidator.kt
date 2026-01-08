package com.giapa.kontroller.util

object IpAddressValidator {

    fun isValidEndpoint(input: String): Boolean {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return false
        if (':' in trimmed) return false
        return isValidIpv4(trimmed)
    }

    private fun isValidIpv4(host: String): Boolean {
        val parts = host.split('.')
        if (parts.size != 4) return false

        return parts.all { part ->
            if (part.isEmpty()) return@all false
            if (part.any { !it.isDigit() }) return@all false
            if (part.length > 1 && part.startsWith('0')) return@all false
            val n = part.toIntOrNull() ?: return@all false
            n in 0..255
        }
    }
}
