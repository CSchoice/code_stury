package stquokka.codeStudy.common.exception.User;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.UserErrorCode;

public class UserIdNotValidException extends BaseException {
    public UserIdNotValidException() {
        super(UserErrorCode.USERID_NOT_VALID);
    }
}
