package stquokka.codeStudy.common.exception.security;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.SecurityErrorCode;

public class InvalidTokenException extends BaseException {

    public InvalidTokenException() {
        super(SecurityErrorCode.INVALID_TOKEN);
    }
    
    public InvalidTokenException(String message) {
        super(SecurityErrorCode.INVALID_TOKEN, message);
    }
}
