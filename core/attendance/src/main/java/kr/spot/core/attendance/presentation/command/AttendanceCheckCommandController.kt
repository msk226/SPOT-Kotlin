package kr.spot.core.attendance.presentation.command

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "출석 체크")
@RestController
@RequestMapping("/api/attendances")
class AttendanceCheckCommandController {
}
