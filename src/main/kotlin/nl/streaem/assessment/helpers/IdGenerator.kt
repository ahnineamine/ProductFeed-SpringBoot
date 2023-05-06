package nl.streaem.assessment.helpers

import java.util.concurrent.atomic.AtomicInteger

object IdGenerator {
    private val atomicInteger = AtomicInteger(1)
    fun nextId() = atomicInteger.getAndIncrement().toLong()
}