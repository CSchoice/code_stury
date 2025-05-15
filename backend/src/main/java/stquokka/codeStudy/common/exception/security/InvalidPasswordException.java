package stquokka.codeStudy.common.exception.security;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.SecurityErrorCode;

public class InvalidPasswordException extends BaseException {
    public InvalidPasswordException() {
        super(SecurityErrorCode.INVALID_PASSWORD);
    }
}
