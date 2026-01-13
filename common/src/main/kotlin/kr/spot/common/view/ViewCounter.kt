package kr.spot.common.view

interface ViewCounter {
    fun incrementAndGet(
        type: ViewableType,
        targetId: Long
    ): Long

    fun currentDelta(
        type: ViewableType,
        targetId: Long
    ): Long
}
