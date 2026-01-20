package kr.spot.worker.admin.config

import kr.spot.common.event.Topics
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.TopicBuilder

@Configuration
class KafkaTopicConfig {

    @Bean
    fun memberEventsTopic(): NewTopic =
        TopicBuilder
            .name(Topics.MEMBER_EVENTS)
            .partitions(3)
            .replicas(1)
            .build()

    @Bean
    fun attendanceEventsTopic(): NewTopic =
        TopicBuilder
            .name(Topics.ATTENDANCE_EVENTS)
            .partitions(3)
            .replicas(1)
            .build()

    @Bean
    fun pointEventsTopic(): NewTopic =
        TopicBuilder
            .name(Topics.POINT_EVENTS)
            .partitions(3)
            .replicas(1)
            .build()

    @Bean
    fun studyEventsTopic(): NewTopic =
        TopicBuilder
            .name(Topics.STUDY_EVENTS)
            .partitions(3)
            .replicas(1)
            .build()

    @Bean
    fun notificationEventsTopic(): NewTopic =
        TopicBuilder
            .name(Topics.NOTIFICATION_EVENTS)
            .partitions(3)
            .replicas(1)
            .build()
}
