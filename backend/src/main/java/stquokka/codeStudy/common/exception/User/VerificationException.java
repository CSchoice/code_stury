package stquokka.codeStudy.common.exception.User;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.UserErrorCode;

public class VerificationException extends BaseException {
    public VerificationException() {
        super(UserErrorCode.VERIFICATION_FAILURE);
    }
}
