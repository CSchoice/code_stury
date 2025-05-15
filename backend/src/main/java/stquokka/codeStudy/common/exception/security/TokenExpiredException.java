package stquokka.codeStudy.common.exception.security;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.SecurityErrorCode;

public class TokenExpiredException extends BaseException {
    public TokenExpiredException() {
        super(SecurityErrorCode.TOKEN_EXPIRED);
    }
}
