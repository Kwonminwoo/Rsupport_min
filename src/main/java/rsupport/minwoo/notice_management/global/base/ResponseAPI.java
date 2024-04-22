package rsupport.minwoo.notice_management.global.base;

import lombok.Builder;
import lombok.Getter;

@Getter
public final class ResponseAPI<T> {

    private final T data;
    private final String message;
    private final int code;

    @Builder
    private ResponseAPI(T data, String message, int code) {
        this.data = data;
        this.message = message;
        this.code = code;
    }

    public static <T> ResponseAPI<T> response(T data, String message) {
        return ResponseAPI.<T>builder()
            .data(data)
            .message(message)
            .build();
    }

    public static ResponseAPI<Void> response(String message) {
        return ResponseAPI.<Void>builder()
            .message(message)
            .build();
    }


    public static <T> ResponseAPI<T> response(T data, String message, int code) {
        return ResponseAPI.<T>builder()
            .data(data)
            .message(message)
            .code(code)
            .build();
    }


}
