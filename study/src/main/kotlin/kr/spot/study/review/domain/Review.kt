package kr.spot.study.review.domain

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Id
import kr.spot.study.global.domain.BaseEntity
import kr.spot.study.review.domain.vo.Content
import kr.spot.study.review.domain.vo.WriterInfo
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@SQLDelete(sql = "UPDATE review SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class Review private constructor(
    @Id
    val id: Long,
    val studyId: Long,
    @Embedded
    val writerInfo: WriterInfo,
    @Embedded
    val content: Content,
    private val isPrivate: Boolean
) : BaseEntity() {
    fun isPrivate(): Boolean = isPrivate == true

    companion object {
        fun of(
            id: Long,
            studyId: Long,
            writerInfo: WriterInfo,
            content: Content,
            isPrivate: Boolean?
        ): Review =
            Review(
                id = id,
                studyId = studyId,
                writerInfo = writerInfo,
                content = content,
                isPrivate = isPrivate == true
            )
    }
}
