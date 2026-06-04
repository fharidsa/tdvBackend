package org.tdv.tdvbackend.service

import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class PasswordServiceTest {

    private val passwordService = PasswordService()

    @Test
    fun hashAndVerify() {
        val hash = passwordService.hash("secret-value")
        assertTrue(passwordService.matches("secret-value", hash))
    }
}
