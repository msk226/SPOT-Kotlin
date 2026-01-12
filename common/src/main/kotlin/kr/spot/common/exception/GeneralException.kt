package kr.spot.common.exception

import kr.spot.common.api.status.ErrorStatus

class GeneralException(
    val status: ErrorStatus
) : RuntimeException(status.message)
