package kr.spot.study.core.infrastructure.jpa

import kr.spot.study.core.domain.association.StudyRegion
import org.springframework.data.jpa.repository.JpaRepository

interface StudyRegionRepository : JpaRepository<StudyRegion, Long>
