package kr.spot.study.todo.domain

import jakarta.persistence.Entity
import jakarta.persistence.Id
import kr.spot.common.api.status.ErrorStatus
import kr.spot.common.exception.GeneralException
import kr.spot.study.global.domain.BaseEntity
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.LocalDate

@Entity
@SQLDelete(sql = "UPDATE todo SET status = 'INACTIVE' WHERE id = ?")
@SQLRestriction("status = 'ACTIVE'")
class Todo private constructor(
    @Id
    val id: Long,
    val studyId: Long,
    val memberId: Long,
    dueDate: LocalDate,
    content: String,
    isCompleted: Boolean
) : BaseEntity() {
    var dueDate: LocalDate = dueDate
        private set

    var content: String = content
        private set

    var isCompleted: Boolean = isCompleted
        private set

    fun update(
        studyId: Long,
        content: String,
        dueDate: LocalDate,
        memberId: Long
    ) {
        validateAccess(studyId)
        validateOwner(memberId)
        this.content = content
        this.dueDate = dueDate
    }

    fun complete(
        studyId: Long,
        memberId: Long
    ) {
        validateAccess(studyId)
        validateOwner(memberId)
        this.isCompleted = true
    }

    fun uncomplete(
        studyId: Long,
        memberId: Long
    ) {
        validateAccess(studyId)
        validateOwner(memberId)
        this.isCompleted = false
    }

    fun delete(
        studyId: Long,
        memberId: Long
    ) {
        validateAccess(studyId)
        validateOwner(memberId)
        super.delete()
    }

    private fun validateAccess(studyId: Long) {
        if (this.studyId != studyId) {
            throw GeneralException(ErrorStatus.TODO_ACCESS_DENIED)
        }
    }

    private fun validateOwner(memberId: Long) {
        if (this.memberId != memberId) {
            throw GeneralException(ErrorStatus.ONLY_TODO_OWNER_CAN_MODIFY)
        }
    }

    companion object {
        fun of(
            id: Long,
            studyId: Long,
            memberId: Long,
            dueDate: LocalDate,
            content: String
        ): Todo =
            Todo(
                id = id,
                studyId = studyId,
                memberId = memberId,
                dueDate = dueDate,
                content = content,
                isCompleted = false
            )
    }
}
