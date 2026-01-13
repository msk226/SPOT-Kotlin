package kr.spot.common.ports

import kr.spot.common.ports.dto.MemberInfoResponse
import org.springframework.stereotype.Component

@Component
class FakeGetMemberInfoPort : GetMemberInfoPort {
    override fun getMemberInfo(memberIds: List<Long>): Map<Long, MemberInfoResponse> =
        memberIds.associateWith {
            MemberInfoResponse(
                name = "Member$it",
                profileImageUrl = "https://example.com/profiles/$it.png"
            )
        }
}
