package kr.spot.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(
    scanBasePackages = [
        "kr.spot.core",
        "kr.spot.common",
        "kr.spot.member",
        "kr.spot.notification",
        "kr.spot.post",
        "kr.spot.point"
    ]
)
class CoreApplication

fun main(args: Array<String>) {
    runApplication<CoreApplication>(*args)
}
