package stquokka.codeStudy.common.exception.User;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.UserErrorCode;

public class UserIdDuplicateException extends BaseException {
    public UserIdDuplicateException() {
        super(UserErrorCode.USERID_DUPLICATE);
    }
}
