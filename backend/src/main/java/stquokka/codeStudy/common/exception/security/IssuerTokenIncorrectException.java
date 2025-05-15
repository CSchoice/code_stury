package stquokka.codeStudy.common.exception.security;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.SecurityErrorCode;

public class IssuerTokenIncorrectException extends BaseException {
    public IssuerTokenIncorrectException() {
        super(SecurityErrorCode.ISSUER_TOKEN_INCORRECT);
    }
}
