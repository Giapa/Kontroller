package com.giapa.kontroller.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IpAddressValidatorTest {

    @Test
    fun `accepts plain ipv4`() {
        assertTrue(IpAddressValidator.isValidEndpoint("192.168.1.10"))
        assertTrue(IpAddressValidator.isValidEndpoint("0.0.0.0"))
        assertTrue(IpAddressValidator.isValidEndpoint("255.255.255.255"))
    }

    @Test
    fun `rejects ports`() {
        assertFalse(IpAddressValidator.isValidEndpoint("192.168.1.10:80"))
        assertFalse(IpAddressValidator.isValidEndpoint("192.168.1.10:65535"))
    }

    @Test
    fun `rejects invalid ipv4`() {
        assertFalse(IpAddressValidator.isValidEndpoint(""))
        assertFalse(IpAddressValidator.isValidEndpoint("192.168.1"))
        assertFalse(IpAddressValidator.isValidEndpoint("256.1.1.1"))
        assertFalse(IpAddressValidator.isValidEndpoint("192.168.1.1:"))
        assertFalse(IpAddressValidator.isValidEndpoint(":8080"))
        assertFalse(IpAddressValidator.isValidEndpoint("192.168.1.1:abc"))
        assertFalse(IpAddressValidator.isValidEndpoint("192.168.01.1"))
    }
}
