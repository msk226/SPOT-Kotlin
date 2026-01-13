package kr.spot.study.core.infrastructure.jpa.querydsl

import com.querydsl.core.types.Projections
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.spot.study.core.domain.QStudy
import kr.spot.study.core.domain.QStudyMember
import kr.spot.study.core.domain.StudyMember
import kr.spot.study.core.domain.enums.StudyMemberStatus
import kr.spot.study.core.infrastructure.jpa.StudyMemberRepositoryCustom
import kr.spot.study.core.infrastructure.jpa.dto.StudyApplicationInfo
import org.springframework.stereotype.Repository

@Repository
class StudyMemberRepositoryCustomImpl(
    private val queryFactory: JPAQueryFactory
) : StudyMemberRepositoryCustom {
    override fun findMyAppliedStudiesWithStudyInfo(
        memberId: Long,
        status: StudyMemberStatus
    ): List<StudyApplicationInfo> {
        val studyMember = QStudyMember.studyMember
        val study = QStudy.study

        return queryFactory
            .select(
                Projections.constructor(
                    StudyApplicationInfo::class.java,
                    studyMember.id,
                    studyMember.studyId,
                    study.name,
                    study.imageUrl
                )
            ).from(studyMember)
            .join(study)
            .on(studyMember.studyId.eq(study.id))
            .where(
                studyMember.memberId.eq(memberId),
                studyMember.studyMemberStatus.eq(status)
            ).orderBy(studyMember.createdAt.desc())
            .fetch()
    }

    override fun findApplicationsByStudyIdAndStatus(
        studyId: Long,
        status: StudyMemberStatus
    ): List<StudyMember> {
        val studyMember = QStudyMember.studyMember

        return queryFactory
            .selectFrom(studyMember)
            .where(
                studyMember.studyId.eq(studyId),
                studyMember.studyMemberStatus.eq(status)
            ).orderBy(studyMember.createdAt.asc())
            .fetch()
    }
}
