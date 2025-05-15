package stquokka.codeStudy.common.exception.User;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.UserErrorCode;

public class ForbiddenWordContainedException extends BaseException {
    public ForbiddenWordContainedException() {
        super(UserErrorCode.FORBIDDEN_WORD_CONTAINED);
    }
}
