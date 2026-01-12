package kr.spot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpotKotlinApplication

fun main(args: Array<String>) {
    runApplication<SpotKotlinApplication>(*args)
}
