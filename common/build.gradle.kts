// common 모듈 - 공통 라이브러리 (실행 불가)

dependencies {
    // Spring 기본
    implementation("org.springframework.boot:spring-boot-starter")

    // Spring Web (for MultipartFile)
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Jackson Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

    // Kafka
    implementation("org.springframework.kafka:spring-kafka")
}
