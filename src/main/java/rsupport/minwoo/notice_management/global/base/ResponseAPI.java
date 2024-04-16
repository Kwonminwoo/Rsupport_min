package rsupport.minwoo.notice_management.global.base;

public class ResponseAPI<T> {
    private T data;
    private String message;
    private int code;

    public ResponseAPI(T data, String message, int code) {
        this.data = data;
        this.message = message;
        this.code = code;
    }

    public ResponseAPI(String message, int code) {
        this.message = message;
        this.code = code;
    }

    public ResponseAPI(String message) {
        this.message = message;
    }
}
