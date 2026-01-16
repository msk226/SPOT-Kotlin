package kr.spot.common.event

import com.fasterxml.jackson.annotation.JsonValue

enum class EventType(
    @JsonValue val value: String,
    val topic: String
) {
    // Member Events
    MEMBER_CREATED("member.created", Topics.MEMBER_EVENTS),
    MEMBER_PROFILE_UPDATED("member.profile.updated", Topics.MEMBER_EVENTS),
    MEMBER_DELETED("member.deleted", Topics.MEMBER_EVENTS),

    // Study Events
    STUDY_CREATED("study.created", Topics.STUDY_EVENTS),
    STUDY_MEMBER_JOINED("study.member.joined", Topics.STUDY_EVENTS),
    STUDY_APPLICATION_RECEIVED("study.application.received", Topics.STUDY_EVENTS),

    // Notification Events
    NOTIFICATION_SEND("notification.send", Topics.NOTIFICATION_EVENTS);

    companion object {
        private val valueMap = entries.associateBy { it.value }

        fun fromValue(value: String): EventType =
            valueMap[value] ?: throw IllegalArgumentException("Unknown event type: $value")
    }
}
