package rsupport.minwoo.notice_management.global.base;

import lombok.Builder;

public abstract class ResponseAPI<T> {
    private T data;
    private String message;
    private int code;

    @Builder
    private ResponseAPI(T data, String message, int code) {
        this.data = data;
        this.message = message;
        this.code = code;
    }

    public static ResponseAPI<?> response(Object data, String message) {
        return ResponseAPI.builder()
            .data(data)
            .message(message)
            .build();
    }

    public static ResponseAPI<?> response(String message) {
        return ResponseAPI.builder()
            .message(message)
            .build();
    }


    public static ResponseAPI<?> response(Object data, String message, int code) {
        return ResponseAPI.builder()
            .data(data)
            .message(message)
            .code(code)
            .build();
    }


}
