package kr.spot.study.core.domain.association

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import kr.spot.study.global.domain.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "study_region")
@SQLDelete(sql = "UPDATE study_region SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class StudyRegion private constructor(
    @Id
    val id: Long,
    val studyId: Long,
    val regionCode: String
) : BaseEntity() {
    companion object {
        fun of(
            id: Long,
            studyId: Long,
            regionCode: String
        ): StudyRegion = StudyRegion(id, studyId, regionCode)
    }
}
