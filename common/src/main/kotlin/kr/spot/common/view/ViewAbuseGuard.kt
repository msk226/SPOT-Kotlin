package kr.spot.common.view

interface ViewAbuseGuard {
    fun shouldCount(
        type: ViewableType,
        targetId: Long,
        viewerId: Long
    ): Boolean
}
