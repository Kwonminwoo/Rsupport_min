package rsupport.minwoo.notice_management.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    DATA_NOT_FOUND(HttpStatus.NOT_FOUND, "데이터를 찾을 수 없습니다."),
    FILE_SAVE_FAIL(HttpStatus.NOT_ACCEPTABLE, "파일을 저장할 수 없습니다."),
    DUPLICATE_FILE_NAME(HttpStatus.CONFLICT, "파일명이 동일합니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
