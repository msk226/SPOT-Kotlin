package kr.spot.core.member.infrastructure

import kr.spot.core.member.domain.PreferredRegion
import org.springframework.data.jpa.repository.JpaRepository

interface PreferredRegionRepository : JpaRepository<PreferredRegion, Long> {
    fun findAllByMemberId(memberId: Long): List<PreferredRegion>

    fun deleteAllByMemberId(memberId: Long)
}
