package kr.spot.common.id

fun interface IdGenerator {
    fun nextId(): Long
}
