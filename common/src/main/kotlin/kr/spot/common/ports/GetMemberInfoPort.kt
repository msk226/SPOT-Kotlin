package kr.spot.common.ports

import kr.spot.common.ports.dto.MemberInfoResponse

interface GetMemberInfoPort {
    fun getMemberInfo(memberIds: List<Long>): Map<Long, MemberInfoResponse>
}
