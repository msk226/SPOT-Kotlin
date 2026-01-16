package kr.spot.study.core.application.command

import kr.spot.common.id.IdGenerator
import kr.spot.study.core.domain.Study
import kr.spot.study.core.domain.StudyMember
import kr.spot.study.core.domain.association.StudyCategory
import kr.spot.study.core.domain.vo.Fee
import kr.spot.study.core.infrastructure.jpa.StudyCategoryRepository
import kr.spot.study.core.infrastructure.jpa.StudyMemberRepository
import kr.spot.study.core.infrastructure.jpa.StudyRepository
import kr.spot.study.core.presentation.command.dto.request.CreateStudyRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CreateStudyService(
    private val idGenerator: IdGenerator,
    private val studyRepository: StudyRepository,
    private val studyCategoryRepository: StudyCategoryRepository,
    private val studyMemberRepository: StudyMemberRepository
) {
    fun createStudy(
        request: CreateStudyRequest,
        leaderId: Long
    ): Long {
        val studyId = idGenerator.nextId()
        val study =
            Study.of(
                id = studyId,
                leaderId = leaderId,
                name = request.name,
                maxMembers = request.maxMembers,
                fee = if (request.hasFee && request.amount != null) Fee.paid(request.amount) else Fee.free(),
                imageUrl = null,
                description = request.description,
                isOnline = request.isOnline
            )
        val studyMember = StudyMember.create(idGenerator.nextId(), studyId, leaderId)

        studyRepository.save(study)
        studyMemberRepository.save(studyMember)

        saveAllStudyCategories(request, studyId)

        return studyId
    }

    private fun saveAllStudyCategories(
        request: CreateStudyRequest,
        studyId: Long
    ) {
        val studyCategories =
            request.categories.map { category ->
                StudyCategory.of(idGenerator.nextId(), studyId, category)
            }
        studyCategoryRepository.saveAll(studyCategories)
    }
}
