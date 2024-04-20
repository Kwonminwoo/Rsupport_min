package rsupport.minwoo.notice_management.domain.attachedFile.exception;

import rsupport.minwoo.notice_management.global.exception.BaseException;
import rsupport.minwoo.notice_management.global.exception.ErrorCode;

public class FileSaveFailException extends BaseException {

    public FileSaveFailException(
        ErrorCode errorCode) {
        super(errorCode);
    }
}
