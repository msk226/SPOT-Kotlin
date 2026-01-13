package kr.spot.study.core.presentation.command.dto.request

import kr.spot.study.core.domain.enums.Category
import kr.spot.study.core.domain.enums.Style

data class CreateStudyRequest(
    val name: String,
    val maxMembers: Int,
    val hasFee: Boolean,
    val amount: Int?,
    val description: String,
    val isOnline: Boolean?,
    val categories: Set<Category>,
    val styles: Set<Style>,
    val regionCodes: Set<String>
)
