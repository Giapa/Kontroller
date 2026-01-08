package com.giapa.kontroller.util

import java.net.Inet4Address
import java.net.NetworkInterface

object LocalIp {
    fun getLocalIpv4Address(): String? {
        return try {
            NetworkInterface.getNetworkInterfaces().toList()
                .asSequence()
                .filter { it.isUp && !it.isLoopback }
                .flatMap { it.inetAddresses.toList().asSequence() }
                .filterIsInstance<Inet4Address>()
                .map { it.hostAddress }
                .firstOrNull()
        } catch (_: Exception) {
            null
        }
    }

    fun to24SubnetPrefix(ipv4: String): String? {
        val parts = ipv4.split('.')
        if (parts.size != 4) return null
        return parts.take(3).joinToString(".")
    }
}

