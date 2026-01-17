package kr.spot.common.api.status

import kr.spot.common.api.BaseCode

enum class ErrorStatus(
    override val httpStatus: Int,
    override val code: String,
    override val message: String
) : BaseCode {
    // 공통 에러
    INTERNAL_SERVER_ERROR(500, "COMMON500", "서버 내부 오류 발생"),
    BAD_REQUEST(400, "COMMON4000", "잘못된 요청입니다."),
    UNAUTHORIZED(401, "COMMON4001", "인증되지 않은 요청입니다."),
    FORBIDDEN(403, "COMMON4002", "접근이 거부되었습니다."),
    FAIL_TO_UPLOAD_FILE(400, "COMMON4003", "파일 업로드에 실패했습니다."),

    // JWT 관련
    EMPTY_JWT(400, "COMMON4005", "JWT 토큰이 비어있습니다."),
    INVALID_JWT(400, "COMMON4006", "유효하지 않은 JWT token입니다."),
    EXPIRED_JWT(400, "COMMON4007", "만료된 JWT token입니다."),
    UNSUPPORTED_JWT(400, "COMMON4008", "지원되지 않는 JWT token입니다."),
    NO_AUTHORIZED(401, "COMMON4009", "권한이 없습니다."),
    INVALID_REFRESH_TOKEN(400, "COMMON4010", "유효하지 않은 리프레시 토큰입니다."),

    // 회원 관련
    MEMBER_NOT_FOUND(404, "MEMBER404", "회원을 찾을 수 없습니다."),
    INVALID_EMAIL_FORMAT(400, "MEMBER4000", "유효하지 않은 이메일 형식입니다."),
    NAME_CAN_NOT_NULL_OR_EMPTY(400, "MEMBER4001", "이름은 null 또는 공백일 수 없습니다."),
    EMAIL_CAN_NOT_NULL_OR_EMPTY(400, "MEMBER4002", "이메일은 null 또는 공백일 수 없습니다."),
    MEMBER_UNSUPPORTED_LOGIN_TYPE(400, "MEMBER4003", "지원하지 않는 로그인 타입입니다."),
    MEMBER_EMAIL_EXIST(400, "MEMBER4004", "이미 해당 방식으로 가입 내역이 존재하는 이메일입니다."),
    FAIL_TO_UPDATE_NAME(400, "MEMBER4005", "이름 변경에 실패했습니다."),
    CANNOT_WITHDRAW_WITH_ACTIVE_STUDY(400, "MEMBER4006", "운영 중인 스터디가 있어 탈퇴할 수 없습니다."),

    // 스터디 관련
    MAX_MEMBERS_MUST_BE_POSITIVE(400, "STUDY4000", "최대 인원 수는 양수여야 합니다."),
    INVALID_FEE_AMOUNT(400, "STUDY4001", "유효하지 않은 스터디 비용입니다."),
    NO_SUCH_CATEGORY(400, "STUDY4002", "존재하지 않는 카테고리입니다."),
    NO_SUCH_STUDY_MEMBER_STATUS(400, "STUDY4003", "존재하지 않는 스터디 멤버 상태입니다."),
    STUDY_NOT_FOUND(404, "STUDY404", "스터디를 찾을 수 없습니다."),
    STUDY_MEMBER_NOT_FOUND(404, "STUDYMEMBER404", "스터디 멤버를 찾을 수 없습니다."),
    ONLY_LEADER_CAN_ACCESS(403, "STUDY403", "스터디장만 접근 가능합니다."),
    ALREADY_APPLIED_STUDY(400, "STUDY4004", "이미 스터디에 지원한 상태입니다."),
    NOT_PENDING_APPLICATION(400, "STUDY4005", "신청 대기 상태가 아닙니다."),
    ONLY_APPLICANT_CAN_SELF_APPROVE(403, "STUDY4031", "본인만 신청을 최종 승인할 수 있습니다."),
    INVALID_STUDY_MEMBER_STATUS_FOR_SELF_APPROVAL(400, "STUDY4006", "셀프 승인을 위한 유효하지 않은 스터디 멤버 상태입니다."),
    STUDY_ALREADY_APPLIED(400, "STUDY4007", "이미 해당 스터디에 지원한 상태입니다."),
    STUDY_ACCESS_DENIED(403, "STUDY4032", "스터디에 접근할 권한이 없습니다."),
    INVALID_STUDY_ACCESS(403, "STUDY4033", "유효하지 않은 스터디 접근입니다."),
    STUDY_IS_FULL(400, "STUDY4008", "스터디 정원이 가득 찼습니다."),

    // 게시글 관련
    POST_NOT_FOUND(404, "POST404", "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(404, "COMMENT404", "댓글을 찾을 수 없습니다."),
    ONLY_AUTHOR_CAN_MODIFY(403, "POST403", "게시글 및 댓글 수정은 작성자만 가능합니다."),
    COMMENT_NOT_BELONG_TO_POST(400, "COMMENT4000", "댓글이 해당 게시글에 속하지 않습니다."),
    PRIVATE_POST_ACCESS_DENIED(403, "POST4030", "스터디원 전용 게시글입니다."),
    ALREADY_LIKED(400, "POST4000", "이미 좋아요를 누른 게시글입니다."),
    ALREADY_UNLIKED(400, "POST4001", "좋아요를 누르지 않은 게시글입니다."),
    SORT_TYPE_INVALID(400, "POST4002", "유효하지 않은 정렬 타입입니다."),

    // 지역 관련
    REGION_NOT_FOUND(404, "REGION404", "지역을 찾을 수 없습니다."),
    NO_SUCH_REGION(400, "REGION4000", "존재하지 않는 지역 코드입니다."),

    // 일정 관련
    SCHEDULE_NOT_FOUND(404, "SCHEDULE404", "일정을 찾을 수 없습니다."),
    SCHEDULE_ACCESS_DENIED(403, "SCHEDULE403", "해당 스터디에 속하는 일정이 아닙니다."),
    SCHEDULE_QR_CODE_ALREADY_ASSIGNED(400, "SCHEDULE4000", "이미 출석 QR 코드가 할당된 일정입니다."),

    // 출석 관련
    ATTENDANCE_NOT_FOUND(404, "ATTENDANCE404", "출석 정보를 찾을 수 없습니다."),
    ATTENDANCE_NOT_IN_SCHEDULE_TIME(400, "ATTENDANCE4000", "일정 시간 외에는 출석체크를 진행할 수 없습니다."),
    ATTENDANCE_NOT_STARTED(400, "ATTENDANCE4001", "출석체크가 시작되지 않았습니다."),
    ATTENDANCE_ALREADY_CHECKED(400, "ATTENDANCE4002", "이미 출석체크를 완료했습니다."),
    INVALID_ATTENDANCE_CODE(400, "ATTENDANCE4003", "유효하지 않은 출석 코드입니다."),
    ATTENDANCE_STREAK_NOT_FOUND(404, "ATTENDANCE4040", "출석 연속 기록을 찾을 수 없습니다."),

    // 투두 관련
    TODO_NOT_FOUND(404, "TODO404", "투두를 찾을 수 없습니다."),
    TODO_ACCESS_DENIED(403, "TODO403", "해당 스터디에 속하는 투두가 아닙니다."),
    ONLY_TODO_OWNER_CAN_MODIFY(403, "TODO4030", "투두 수정은 작성자만 가능합니다."),

    // 회고 관련
    REVIEW_NOT_FOUND(404, "REVIEW404", "회고를 찾을 수 없습니다."),
    REVIEW_ACCESS_DENIED(403, "REVIEW403", "해당 스터디에 속하는 회고가 아닙니다."),
    ALREADY_REACTED(400, "REVIEW4000", "이미 반응을 누른 회고입니다."),
    REACTION_NOT_FOUND(404, "REVIEW4040", "반응을 찾을 수 없습니다."),

    // 알림 관련
    NOTIFICATION_NOT_FOUND(404, "NOTIFICATION404", "알림을 찾을 수 없습니다."),
    NOTIFICATION_ACCESS_DENIED(403, "NOTIFICATION403", "해당 알림에 접근할 권한이 없습니다."),
    INVALID_NOTIFICATION_TYPE(400, "NOTIFICATION4000", "유효하지 않은 알림 유형입니다."),
    NOTIFICATION_PAYLOAD_MISSING_MEMBER_ID(400, "NOTIFICATION4001", "알림 페이로드에 memberId가 필요합니다."),
    NOTIFICATION_PAYLOAD_MISSING_STUDY_ID(400, "NOTIFICATION4002", "알림 페이로드에 studyId가 필요합니다."),
    NOTIFICATION_TEMPLATE_NOT_FOUND(500, "NOTIFICATION5000", "알림 템플릿을 찾을 수 없습니다."),
    PUSH_NOTIFICATION_FAILED(500, "NOTIFICATION5001", "푸시 알림 발송에 실패했습니다."),

    // 포인트 관련
    POINT_NOT_FOUND(404, "POINT404", "포인트 정보를 찾을 수 없습니다.")
    ;

    override val isSuccess: Boolean = false
}
