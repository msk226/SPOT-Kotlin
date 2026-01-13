package kr.spot.common.ports

import kr.spot.common.ports.dto.UploadResult
import org.springframework.web.multipart.MultipartFile

interface FileStoragePort {
    fun upload(
        file: MultipartFile,
        dir: String
    ): UploadResult

    fun delete(fileUrl: String)
}
