package kr.spot.common.ports.dto

data class UploadResult(
    val url: String,
    val fileName: String
) {
    companion object {
        fun of(
            url: String,
            fileName: String
        ): UploadResult = UploadResult(url, fileName)
    }
}
