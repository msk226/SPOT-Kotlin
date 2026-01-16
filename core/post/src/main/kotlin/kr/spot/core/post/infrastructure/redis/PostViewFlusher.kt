package kr.spot.core.post.infrastructure.redis

import kr.spot.core.post.infrastructure.jpa.PostRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
class PostViewFlusher(
    private val postRepository: PostRepository
) {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun flushPostViews(
        postId: Long,
        delta: Long
    ) {
        postRepository.increaseViewBy(postId, delta)
        log.debug(
            "게시글 조회수 DB 업데이트: postId={}, delta={}",
            postId,
            delta
        )
    }

    companion object {
        private val log =
            LoggerFactory.getLogger(
                PostViewFlusher::class.java
            )
    }
}
