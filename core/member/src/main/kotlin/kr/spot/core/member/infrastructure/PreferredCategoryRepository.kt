package kr.spot.core.member.infrastructure

import kr.spot.core.member.domain.PreferredCategory
import org.springframework.data.jpa.repository.JpaRepository

interface PreferredCategoryRepository : JpaRepository<PreferredCategory, Long> {
    fun findAllByMemberId(memberId: Long): List<PreferredCategory>

    fun deleteAllByMemberId(memberId: Long)
}
