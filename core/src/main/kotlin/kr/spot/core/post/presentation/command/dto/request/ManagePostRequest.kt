package kr.spot.core.post.presentation.command.dto.request

import kr.spot.core.post.domain.enums.PostType

data class ManagePostRequest(
    val title: String,
    val content: String,
    val postType: PostType
)
