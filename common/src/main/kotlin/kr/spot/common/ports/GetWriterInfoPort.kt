package kr.spot.common.ports

import kr.spot.common.ports.dto.WriterInfoResponse

interface GetWriterInfoPort {
    fun get(memberId: Long): WriterInfoResponse
}
