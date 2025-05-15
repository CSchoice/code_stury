package stquokka.codeStudy.common.exception.User;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.UserErrorCode;

public class NotAllowedNicknameException extends BaseException {
    public NotAllowedNicknameException() {
        super(UserErrorCode.NOT_ALLOWED_NICKNAME);
    }
}
