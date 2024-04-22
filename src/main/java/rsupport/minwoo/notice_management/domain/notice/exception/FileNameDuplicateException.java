package rsupport.minwoo.notice_management.domain.notice.exception;

import rsupport.minwoo.notice_management.global.exception.BaseException;
import rsupport.minwoo.notice_management.global.exception.ErrorCode;

public class FileNameDuplicateException extends BaseException {

    public FileNameDuplicateException(
        ErrorCode errorCode) {
        super(errorCode);
    }
}
