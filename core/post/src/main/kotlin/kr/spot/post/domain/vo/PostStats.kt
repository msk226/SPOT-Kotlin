package kr.spot.post.domain.vo

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
        ): kr.spot.post.domain.vo.PostStats =
            _root_ide_package_.kr.spot.post.domain.vo.PostStats(
                views,
                likes,
                comments
            )
    }
}
