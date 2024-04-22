package rsupport.minwoo.notice_management.global.exception;

public class DataNotFoundException extends BaseException {

    public DataNotFoundException() {
        super(ErrorCode.DATA_NOT_FOUND);
    }
}
