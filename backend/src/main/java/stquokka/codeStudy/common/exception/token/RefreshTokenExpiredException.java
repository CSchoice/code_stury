package stquokka.codeStudy.common.exception.token;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.SecurityErrorCode;

public class RefreshTokenExpiredException extends BaseException {
    public RefreshTokenExpiredException() {
        super(SecurityErrorCode.TOKEN_EXPIRED);
    }
}
