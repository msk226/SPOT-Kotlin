plugins {
    kotlin("plugin.jpa")
}

dependencies {
    implementation(project(":common"))

    // Spring Web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Kafka Streams
    implementation("org.apache.kafka:kafka-streams")
    implementation("org.springframework.kafka:spring-kafka")

    // OpenAPI (Swagger)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
}
