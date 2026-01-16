// common 모듈 - 공통 라이브러리 (실행 불가)

plugins {
    kotlin("plugin.jpa")
    kotlin("kapt")
}

dependencies {
    // Spring 기본
    implementation("org.springframework.boot:spring-boot-starter")

    // Spring Web (for MultipartFile)
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Spring Data JPA (for BaseEntity)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Jackson Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Kafka
    implementation("org.springframework.kafka:spring-kafka")

    // QueryDSL (for QBaseEntity)
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    kapt("jakarta.annotation:jakarta.annotation-api")
    kapt("jakarta.persistence:jakarta.persistence-api")
}
