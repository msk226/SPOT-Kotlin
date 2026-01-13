package kr.spot.study.core.infrastructure.jpa

import kr.spot.study.core.domain.association.StudyCategory
import org.springframework.data.jpa.repository.JpaRepository

interface StudyCategoryRepository : JpaRepository<StudyCategory, Long> {
    fun findAllByStudyId(studyId: Long): List<StudyCategory>
}
