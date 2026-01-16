package kr.spot.study

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["kr.spot.study", "kr.spot.common"])
class StudyApplication

fun main(args: Array<String>) {
    runApplication<StudyApplication>(*args)
}
