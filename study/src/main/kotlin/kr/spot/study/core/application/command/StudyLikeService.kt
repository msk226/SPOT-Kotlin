package kr.spot.study.core.application.command

import kr.spot.common.id.IdGenerator
import kr.spot.study.core.infrastructure.jpa.StudyLikeRepository
import kr.spot.study.core.infrastructure.jpa.StudyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class StudyLikeService(
    private val idGenerator: IdGenerator,
    private val studyLikeRepository: StudyLikeRepository,
    private val studyRepository: StudyRepository
) {
    fun likeStudy(
        studyId: Long,
        memberId: Long
    ) {
        val inserted = studyLikeRepository.saveStudyLike(idGenerator.nextId(), studyId, memberId)
        increaseLikeCount(studyId, inserted)
    }

    private fun increaseLikeCount(
        studyId: Long,
        inserted: Int
    ) {
        if (inserted == 1) {
            studyRepository.increaseLike(studyId)
        }
    }

    fun unlikeStudy(
        studyId: Long,
        memberId: Long
    ) {
        val deleted = studyLikeRepository.hardDelete(studyId, memberId)
        if (deleted > 0) {
            studyRepository.decreaseLike(studyId)
        }
    }
}
