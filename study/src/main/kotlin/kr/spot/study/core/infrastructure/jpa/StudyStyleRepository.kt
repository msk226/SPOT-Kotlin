package kr.spot.study.core.infrastructure.jpa

import kr.spot.study.core.domain.association.StudyStyle
import org.springframework.data.jpa.repository.JpaRepository

interface StudyStyleRepository : JpaRepository<StudyStyle, Long>
