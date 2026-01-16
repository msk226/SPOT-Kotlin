package kr.spot.post.presentation.command.dto.request

import kr.spot.post.domain.enums.PostType

data class ManagePostRequest(
    val title: String,
    val content: String,
    val postType: kr.spot.post.domain.enums.PostType
)
