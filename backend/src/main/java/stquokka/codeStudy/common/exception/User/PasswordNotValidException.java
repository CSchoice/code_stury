package stquokka.codeStudy.common.exception.User;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.UserErrorCode;

public class PasswordNotValidException extends BaseException {
    public PasswordNotValidException() {
        super(UserErrorCode.PASSWORD_NOT_VALID);
    }
}
