package stquokka.codeStudy.common.exception.global;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.GlobalErrorCode;

public class AccessDeniedRequestException extends BaseException {
        public AccessDeniedRequestException() {
            super(GlobalErrorCode.ACCESS_DENIED_REQUEST);
        }
}
