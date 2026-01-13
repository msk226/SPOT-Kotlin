package kr.spot.study.core.infrastructure.batch

import kr.spot.study.core.infrastructure.jpa.StudyRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class StudyViewFlusher(
    private val studyRepository: StudyRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun updateViewCount(
        studyId: Long,
        delta: Long
    ) {
        studyRepository.increaseViewBy(studyId, delta)
        log.debug("스터디 조회수 DB 업데이트: studyId={}, delta={}", studyId, delta)
    }
}
