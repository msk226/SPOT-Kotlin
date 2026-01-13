package kr.spot.study.core.domain.enums

enum class FeeCategory {
    NONE,
    BELOW_10K,
    FROM_10K_TO_20K,
    FROM_20K_TO_30K,
    FROM_30K_TO_40K,
    FROM_40K_TO_50K,
    ABOVE_50K;

    companion object {
        fun getFeeCategory(amount: Int): FeeCategory =
            when (amount) {
                0 -> NONE
                in 1..10_000 -> BELOW_10K
                in 10_001..20_000 -> FROM_10K_TO_20K
                in 20_001..30_000 -> FROM_20K_TO_30K
                in 30_001..40_000 -> FROM_30K_TO_40K
                in 40_001..50_000 -> FROM_40K_TO_50K
                else -> ABOVE_50K
            }
    }
}
