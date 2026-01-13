package kr.spot.study.core.application.query

import kr.spot.common.view.ViewAbuseGuard
import kr.spot.common.view.ViewCounter
import kr.spot.common.view.ViewableType
import kr.spot.study.core.domain.Study
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class StudyViewCountService(
    private val viewCounter: ViewCounter,
    private val viewAbuseGuard: ViewAbuseGuard
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun calculateDisplayViewCount(
        study: Study,
        studyId: Long,
        viewerId: Long
    ): Long {
        val baseViewCount = study.viewCount
        val viewDelta = getViewDeltaFromCounter(studyId, viewerId)
        return baseViewCount + viewDelta
    }

    private fun getViewDeltaFromCounter(
        studyId: Long,
        viewerId: Long
    ): Long =
        try {
            if (viewAbuseGuard.shouldCount(ViewableType.STUDY, studyId, viewerId)) {
                viewCounter.incrementAndGet(ViewableType.STUDY, studyId)
            } else {
                viewCounter.currentDelta(ViewableType.STUDY, studyId)
            }
        } catch (e: Exception) {
            log.warn("Redis view counter access failed for studyId: {}", studyId, e)
            0L
        }
}
