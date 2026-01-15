package kr.spot.common.serializer

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory

object DataSerializer {
    @PublishedApi
    internal val log = LoggerFactory.getLogger(javaClass)

    @PublishedApi
    internal val objectMapper: ObjectMapper = ObjectMapper()
        .registerModule(JavaTimeModule())
        .registerModule(KotlinModule.Builder().build())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    fun <T> deserialize(data: String, clazz: Class<T>): T? {
        return runCatching {
            objectMapper.readValue(data, clazz)
        }.onFailure {
            log.error("[DataSerializer.deserialize] data={}, clazz={}", data, clazz, it)
        }.getOrNull()
    }

    inline fun <reified T> deserialize(data: String): T? {
        return runCatching {
            objectMapper.readValue<T>(data)
        }.onFailure {
            log.error("[DataSerializer.deserialize] data={}, clazz={}", data, T::class.java, it)
        }.getOrNull()
    }

    fun <T> convert(data: Any, clazz: Class<T>): T {
        return objectMapper.convertValue(data, clazz)
    }

    inline fun <reified T> convert(data: Any): T {
        return objectMapper.convertValue(data, T::class.java)
    }

    fun serialize(obj: Any): String? {
        return runCatching {
            objectMapper.writeValueAsString(obj)
        }.onFailure {
            log.error("[DataSerializer.serialize] object={}", obj, it)
        }.getOrNull()
    }
}
