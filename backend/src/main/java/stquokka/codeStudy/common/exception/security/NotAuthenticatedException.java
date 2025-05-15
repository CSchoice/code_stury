package stquokka.codeStudy.common.exception.security;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.SecurityErrorCode;

public class NotAuthenticatedException extends BaseException {
        public NotAuthenticatedException() {
            super(SecurityErrorCode.NOT_AUTHENTICATED_ERROR);
        }
}
