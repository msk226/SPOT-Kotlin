package kr.spot.study.core.domain.association

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import kr.spot.study.global.domain.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(
    name = "study_like",
    uniqueConstraints = [UniqueConstraint(name = "uk_study_member", columnNames = ["study_id", "member_id"])]
)
@SQLDelete(sql = "UPDATE study_like SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class StudyLike private constructor(
    @Id
    val id: Long,
    val studyId: Long,
    val memberId: Long
) : BaseEntity() {
    companion object {
        fun of(
            id: Long,
            studyId: Long,
            memberId: Long
        ): StudyLike = StudyLike(id, studyId, memberId)
    }
}
