package kr.spot.study.core.infrastructure.jpa

import kr.spot.study.core.domain.association.StudyLike
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface StudyLikeRepository : JpaRepository<StudyLike, Long> {
    @Modifying
    @Query(
        value = """
            INSERT IGNORE INTO study_like(id, study_id, member_id, status, created_at, updated_at)
            VALUES (:id, :studyId, :memberId, 'ACTIVE', NOW(), NOW())
        """,
        nativeQuery = true
    )
    fun saveStudyLike(
        @Param("id") id: Long,
        @Param("studyId") studyId: Long,
        @Param("memberId") memberId: Long
    ): Int

    @Modifying
    @Query(
        value = """
            DELETE FROM study_like
             WHERE study_id = :studyId
               AND member_id = :memberId
        """,
        nativeQuery = true
    )
    fun hardDelete(
        @Param("studyId") studyId: Long,
        @Param("memberId") memberId: Long
    ): Int

    @Modifying
    @Query(value = "DELETE FROM study_like WHERE member_id = :memberId", nativeQuery = true)
    fun deleteAllByMemberId(
        @Param("memberId") memberId: Long
    )

    @Query("SELECT sl.studyId FROM StudyLike sl WHERE sl.memberId = :memberId")
    fun findStudyIdsByMemberId(
        @Param("memberId") memberId: Long
    ): Set<Long>
}
