package kr.spot.study.core.domain.association

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.spot.study.core.domain.enums.Style
import kr.spot.study.global.domain.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "study_style")
@SQLDelete(sql = "UPDATE study_style SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class StudyStyle private constructor(
    @Id
    val id: Long,
    val studyId: Long,
    @Enumerated(EnumType.STRING)
    val style: Style
) : BaseEntity() {
    companion object {
        fun of(
            id: Long,
            studyId: Long,
            style: Style
        ): StudyStyle = StudyStyle(id, studyId, style)
    }
}
