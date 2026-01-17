plugins {
    kotlin("plugin.jpa")
}

dependencies {
    implementation(project(":common"))

    // Spring Data JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Spring Web
    implementation("org.springframework.boot:spring-boot-starter-web")

    // OpenAPI (Swagger)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    // Kafka
    implementation("org.springframework.kafka:spring-kafka")
}
