package stquokka.codeStudy.common.exception.User;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.UserErrorCode;

public class EmailNotFoundException extends BaseException {
    public EmailNotFoundException() {
        super(UserErrorCode.EMAIL_NOT_FOUND);
    }
}
