package stquokka.codeStudy.common.exception.User;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.UserErrorCode;

public class UserDuplicateException extends BaseException {
    public UserDuplicateException() {
        super(UserErrorCode.USER_DUPLICATE);
    }
}
