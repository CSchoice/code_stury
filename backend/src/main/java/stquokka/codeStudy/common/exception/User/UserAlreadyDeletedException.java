package stquokka.codeStudy.common.exception.User;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.UserErrorCode;

public class UserAlreadyDeletedException extends BaseException {
    public UserAlreadyDeletedException() {
        super(UserErrorCode.USER_ALREADY_DELETED);
    }
}
