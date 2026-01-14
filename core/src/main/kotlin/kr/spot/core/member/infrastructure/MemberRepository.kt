package kr.spot.core.member.infrastructure

import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.exception.GeneralException
import kr.spot.core.member.domain.Member
import kr.spot.core.member.domain.enums.LoginType
import kr.spot.core.member.domain.vo.Email
import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {

}
