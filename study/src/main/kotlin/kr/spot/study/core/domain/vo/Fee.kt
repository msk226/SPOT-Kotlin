package kr.spot.study.core.domain.vo

import jakarta.persistence.Embeddable
import kr.spot.study.core.domain.enums.FeeCategory

@Embeddable
data class Fee(
    val hasFee: Boolean,
    val amount: Int?,
    val feeCategory: FeeCategory
) {
    companion object {
        fun free() =
            Fee(
                hasFee = false,
                amount = null,
                feeCategory = FeeCategory.NONE
            )

        fun paid(amount: Int) =
            Fee(
                hasFee = true,
                amount = amount,
                feeCategory = FeeCategory.getFeeCategory(amount)
            )
    }
}
