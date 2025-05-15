package stquokka.codeStudy.common.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import stquokka.codeStudy.api.CommonResponse;
import stquokka.codeStudy.common.exception.User.InvalidLoginInfoException;
import stquokka.codeStudy.common.exception.global.AccessDeniedRequestException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidLoginInfoException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public CommonResponse<Void>handleInvalidLoginInfoException(InvalidLoginInfoException e) {
        log.error("InvalidLoginInfoException Error", e);
        return CommonResponse.unauthorized(e.getErrorCode());
    }

    @ExceptionHandler(AccessDeniedRequestException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CommonResponse<Void>handleAccessDeniedRequestException(AccessDeniedRequestException e) {
        log.error("AccessDeniedRequestException Error", e);
        return CommonResponse.forbidden(e.getErrorCode());
    }

}
