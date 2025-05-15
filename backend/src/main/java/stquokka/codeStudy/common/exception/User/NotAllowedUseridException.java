package stquokka.codeStudy.common.exception.User;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.UserErrorCode;

public class NotAllowedUseridException extends BaseException {
    public NotAllowedUseridException() {
        super(UserErrorCode.NOT_ALLOWED_USERID);
    }
}
