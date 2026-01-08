package com.giapa.kontroller.util

object IpAddressValidator {

    fun isValidEndpoint(input: String): Boolean {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return false

        val (host, port) = splitHostPort(trimmed) ?: return false
        if (!isValidIpv4(host)) return false

        if (port != null) {
            val p = port.toIntOrNull() ?: return false
            if (p !in 1..65535) return false
        }

        return true
    }

    private fun splitHostPort(value: String): Pair<String, String?>? {
        val first = value.indexOf(':')
        if (first == -1) return value to null
        if (value.indexOf(':', startIndex = first + 1) != -1) return null

        val host = value.substring(0, first)
        val port = value.substring(first + 1)

        if (host.isBlank() || port.isBlank()) return null
        return host to port
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

