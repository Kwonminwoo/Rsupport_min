package rsupport.minwoo.notice_management.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rsupport.minwoo.notice_management.global.base.ResponseAPI;

@RestControllerAdvice
public class BaseExceptionControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ResponseAPI<Void>> baseException(BaseException e) {
        ErrorCode error = e.getErrorCode();
        e.printStackTrace();
        return ResponseEntity.status(error.getHttpStatus())
            .body(ResponseAPI.response(error.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ResponseAPI<Void>> validException(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        e.printStackTrace();
        return ResponseEntity.status(e.getStatusCode()).body(ResponseAPI.response(errorMessage));
    }
}
