package kr.spot.study.core.infrastructure.jpa.querydsl

import com.querydsl.core.types.OrderSpecifier
import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.JPAExpressions.selectOne
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.spot.study.core.domain.QStudy
import kr.spot.study.core.domain.QStudyMember
import kr.spot.study.core.domain.Study
import kr.spot.study.core.domain.association.QStudyCategory
import kr.spot.study.core.domain.enums.Category
import kr.spot.study.core.domain.enums.FeeCategory
import kr.spot.study.core.domain.enums.RecruitingStatus
import kr.spot.study.core.domain.enums.SortBy
import kr.spot.study.core.domain.enums.StudyMemberStatus
import org.springframework.stereotype.Repository

@Repository
@Suppress("TooManyFunctions")
class StudyQueryRepository(
    private val query: JPAQueryFactory
) {
    
    @Suppress("LongParameterList")
    fun findRecruitingStudies(
        feeCategory: FeeCategory?,
        categories: List<Category>?,
        isOnline: Boolean?,
        sortBy: SortBy?,
        cursor: Long?,
        limit: Int
    ): List<Study> {
        val study = QStudy.study

        return query
            .select(study)
            .from(study)
            .where(
                study.recruitingStatus.eq(RecruitingStatus.RECRUITING),
                existsCategories(study, categories),
                eqFeeCategory(feeCategory, study),
                eqIsOnline(isOnline, study),
                ltCursor(cursor, study)
            ).orderBy(
                orderBy(sortBy, study),
                study.id.desc()
            ).limit(limit.toLong())
            .fetch()
    }

    fun countRecruitingStudies(
        feeCategory: FeeCategory?,
        categories: List<Category>?,
        isOnline: Boolean?
    ): Long {
        val study = QStudy.study

        return query
            .select(study.id.count())
            .from(study)
            .where(
                study.recruitingStatus.eq(RecruitingStatus.RECRUITING),
                existsCategories(study, categories),
                eqFeeCategory(feeCategory, study),
                eqIsOnline(isOnline, study)
            ).fetchOne() ?: 0L
    }

    private fun eqFeeCategory(
        feeCategory: FeeCategory?,
        study: QStudy
    ): BooleanExpression? =
        feeCategory?.let {
            study.fee.feeCategory.eq(it)
        }

    private fun eqIsOnline(
        isOnline: Boolean?,
        study: QStudy
    ): BooleanExpression? = isOnline?.let { study.isOnline.eq(it) }

    private fun ltCursor(
        cursor: Long?,
        study: QStudy
    ): BooleanExpression? = cursor?.let { study.id.lt(it) }

    private fun existsCategories(
        study: QStudy,
        categories: List<Category>?
    ): BooleanExpression? {
        if (categories.isNullOrEmpty()) {
            return null
        }
        val studyCategory = QStudyCategory.studyCategory
        return selectOne()
            .from(studyCategory)
            .where(
                studyCategory.studyId.eq(study.id),
                studyCategory.category.`in`(categories)
            ).exists()
    }

    private fun orderBy(
        sortBy: SortBy?,
        study: QStudy
    ): OrderSpecifier<*> =
        when (sortBy) {
            SortBy.RECENT, null -> study.id.desc()
            SortBy.LIKES -> study.likeCount.desc()
            SortBy.HITS -> study.viewCount.desc()
        }
}
