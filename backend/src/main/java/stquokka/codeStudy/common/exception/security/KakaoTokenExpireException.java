package stquokka.codeStudy.common.exception.security;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.SecurityErrorCode;

public class KakaoTokenExpireException extends BaseException {
    public KakaoTokenExpireException() {
        super(SecurityErrorCode.KAKAO_TOKEN_EXPIRE);
    }
}
