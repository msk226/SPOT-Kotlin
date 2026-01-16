package kr.spot.core.post.domain.vo

import jakarta.persistence.Embeddable

@Embeddable
class PostStats(
    val views: Long,
    val likes: Long,
    val comments: Long
) {
    companion object {
        fun of(
            views: Long,
            likes: Long,
            comments: Long
        ): PostStats =
            PostStats(
                views,
                likes,
                comments
            )
    }
}
