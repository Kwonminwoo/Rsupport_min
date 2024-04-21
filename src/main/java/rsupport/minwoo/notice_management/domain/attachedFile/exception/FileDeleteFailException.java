package rsupport.minwoo.notice_management.domain.attachedFile.exception;

import rsupport.minwoo.notice_management.global.exception.BaseException;
import rsupport.minwoo.notice_management.global.exception.ErrorCode;

public class FileDeleteFailException extends BaseException {

    public FileDeleteFailException(
        ErrorCode errorCode) {
        super(errorCode);
    }
}
