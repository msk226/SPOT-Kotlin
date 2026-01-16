package kr.spot.core.member.infrastructure

import kr.spot.core.member.domain.Member
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<kr.spot.core.member.domain.Member, Long>
