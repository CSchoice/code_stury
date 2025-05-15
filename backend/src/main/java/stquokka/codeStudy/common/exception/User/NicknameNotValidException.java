package stquokka.codeStudy.common.exception.User;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.UserErrorCode;

public class NicknameNotValidException extends BaseException {
    public NicknameNotValidException() {
        super(UserErrorCode.NICKNAME_NOT_VALID);
    }
}
