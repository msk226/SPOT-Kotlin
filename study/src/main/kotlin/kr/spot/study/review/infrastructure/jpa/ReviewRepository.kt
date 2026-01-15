package kr.spot.study.review.infrastructure.jpa

import kr.spot.study.review.domain.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface ReviewRepository : JpaRepository<Review, Long> {

    @Modifying
    @Query(
        """
        UPDATE Review r
        SET r.writerInfo.writerName = :nickname,
            r.writerInfo.writerProfileImageUrl = :profileImageUrl
        WHERE r.writerInfo.writerId = :memberId
        """
    )
    fun updateWriterInfo(memberId: Long, nickname: String, profileImageUrl: String?): Int
}
