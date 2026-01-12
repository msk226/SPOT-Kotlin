package kr.spot.common.id

import kotlin.random.Random

class Snowflake(
    private val nodeId: Long = Random.nextLong(MAX_NODE_ID + 1)
) : IdGenerator {
    private var lastTimeMillis: Long = START_TIME_MILLIS
    private var sequence: Long = 0L

    init {
        require(nodeId in 0..MAX_NODE_ID) {
            "Node ID must be between 0 and $MAX_NODE_ID"
        }
    }

    @Synchronized
    override fun nextId(): Long {
        var currentTimeMillis = System.currentTimeMillis()

        check(currentTimeMillis >= lastTimeMillis) {
            "Clock moved backwards. Refusing to generate id"
        }

        if (currentTimeMillis == lastTimeMillis) {
            sequence = (sequence + 1) and MAX_SEQUENCE
            if (sequence == 0L) {
                currentTimeMillis = waitNextMillis(currentTimeMillis)
            }
        } else {
            sequence = 0L
        }

        lastTimeMillis = currentTimeMillis

        return ((currentTimeMillis - START_TIME_MILLIS) shl (NODE_ID_BITS + SEQUENCE_BITS)) or
            (nodeId shl SEQUENCE_BITS) or
            sequence
    }

    private fun waitNextMillis(currentTimestamp: Long): Long {
        var timestamp = currentTimestamp
        while (timestamp <= lastTimeMillis) {
            timestamp = System.currentTimeMillis()
        }
        return timestamp
    }

    companion object {
        private const val NODE_ID_BITS = 10
        private const val SEQUENCE_BITS = 12

        private const val MAX_NODE_ID = (1L shl NODE_ID_BITS) - 1
        private const val MAX_SEQUENCE = (1L shl SEQUENCE_BITS) - 1

        // Epoch: 2024-01-01 00:00:00 UTC
        private const val START_TIME_MILLIS = 1704067200000L
    }
}
