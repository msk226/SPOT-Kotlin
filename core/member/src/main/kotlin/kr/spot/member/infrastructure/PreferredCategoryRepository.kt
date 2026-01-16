package kr.spot.member.infrastructure

import kr.spot.member.domain.PreferredCategory
import org.springframework.data.jpa.repository.JpaRepository

interface PreferredCategoryRepository : JpaRepository<PreferredCategory, Long> {
    fun findAllByMemberId(memberId: Long): List<PreferredCategory>

    fun deleteAllByMemberId(memberId: Long)
}
