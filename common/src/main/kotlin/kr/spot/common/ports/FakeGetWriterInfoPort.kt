package kr.spot.common.ports

import kr.spot.common.ports.dto.WriterInfoResponse
import org.springframework.stereotype.Component

@Component
class FakeGetWriterInfoPort : GetWriterInfoPort {
    override fun get(memberId: Long): WriterInfoResponse =
        WriterInfoResponse.of(
            writerId = memberId,
            nickname = "Writer$memberId",
            profileImageUrl = "https://example.com/writers/$memberId.png"
        )
}
