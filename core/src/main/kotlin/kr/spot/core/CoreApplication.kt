package kr.spot.core

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["kr.spot.core", "kr.spot.common"])
class CoreApplication

fun main(args: Array<String>) {
    runApplication<CoreApplication>(*args)
}
