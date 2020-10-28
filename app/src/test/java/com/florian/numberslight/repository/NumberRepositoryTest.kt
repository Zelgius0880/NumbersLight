package com.florian.numberslight.repository

import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertTrue

class NumberRepositoryTest {

    private val repository = NumberRepository(true)

    @Test
    fun testGetList() {
        runBlocking {
            with(repository.getList()) {
                assertTrue(this != null)
                assertTrue(this!!.isNotEmpty())
            }
        }
    }

    @Test
    fun testGetNumber() {
        runBlocking {
            val item = repository.getList()!!.first()
            with(repository.getNumber(item)) {
                assertTrue(this != null)
                assertTrue(item.name == this!!.name)
            }
        }
    }
}