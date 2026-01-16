plugins {
    kotlin("plugin.jpa")
}

dependencies {
    implementation(project(":common"))

    // Spring Data JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
