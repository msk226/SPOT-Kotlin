plugins {
    id("org.springframework.boot")
    kotlin("plugin.jpa")
}

dependencies {
    implementation(project(":common"))

    // Spring
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Database
    runtimeOnly("com.mysql:mysql-connector-j")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Kafka
    implementation("org.springframework.kafka:spring-kafka")

    // Actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")
}
