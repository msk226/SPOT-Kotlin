package kr.spot.study.review.infrastructure.jpa

import kr.spot.study.review.domain.Review
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository : JpaRepository<Review, Long>
