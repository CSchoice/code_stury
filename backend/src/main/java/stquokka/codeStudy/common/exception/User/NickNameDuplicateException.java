package stquokka.codeStudy.common.exception.User;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.UserErrorCode;

public class NickNameDuplicateException extends BaseException {
    public NickNameDuplicateException() {
        super(UserErrorCode.NICKNAME_DUPLICATE);
    }
}
