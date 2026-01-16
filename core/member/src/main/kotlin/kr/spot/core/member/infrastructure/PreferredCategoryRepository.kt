package kr.spot.core.member.infrastructure

import kr.spot.core.member.domain.PreferredCategory
import org.springframework.data.jpa.repository.JpaRepository

interface PreferredCategoryRepository : JpaRepository<kr.spot.core.member.domain.PreferredCategory, Long> {
    fun findAllByMemberId(memberId: Long): List<kr.spot.core.member.domain.PreferredCategory>

    fun deleteAllByMemberId(memberId: Long)
}
