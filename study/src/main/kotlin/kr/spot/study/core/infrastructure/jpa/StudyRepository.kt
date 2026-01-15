package kr.spot.study.core.infrastructure.jpa

import kr.spot.common.api.exception.GeneralException
import kr.spot.common.api.status.ErrorStatus
import kr.spot.study.core.domain.Study
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface StudyRepository : JpaRepository<Study, Long> {
    fun getStudyById(studyId: Long): Study =
        findById(studyId)
            .orElseThrow { GeneralException(ErrorStatus.STUDY_NOT_FOUND) }

    @Modifying
    @Query(
        """
        update Study s
           set s.viewCount = s.viewCount + :delta,
               s.updatedAt = CURRENT_TIMESTAMP
         where s.id = :studyId
        """
    )
    fun increaseViewBy(
        @Param("studyId") studyId: Long,
        @Param("delta") delta: Long
    ): Int

    @Modifying
    @Query("update Study s set s.likeCount = s.likeCount + 1 where s.id = :studyId")
    fun increaseLike(
        @Param("studyId") studyId: Long
    ): Int

    @Modifying
    @Query(
        """
        update Study s
           set s.likeCount = case when s.likeCount > 0 then s.likeCount - 1 else 0 end
         where s.id = :studyId
        """
    )
    fun decreaseLike(
        @Param("studyId") studyId: Long
    ): Int
}
