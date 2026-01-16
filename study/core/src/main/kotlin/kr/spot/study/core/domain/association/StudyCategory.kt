package kr.spot.study.core.domain.association

import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.spot.common.domain.BaseEntity
import kr.spot.study.core.domain.enums.Category
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "study_category")
@SQLDelete(sql = "UPDATE study_category SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class StudyCategory private constructor(
    @Id
    val id: Long,
    val studyId: Long,
    @Enumerated(EnumType.STRING)
    val category: Category
) : BaseEntity() {
    companion object {
        fun of(
            id: Long,
            studyId: Long,
            category: Category
        ): StudyCategory = StudyCategory(id, studyId, category)
    }
}
