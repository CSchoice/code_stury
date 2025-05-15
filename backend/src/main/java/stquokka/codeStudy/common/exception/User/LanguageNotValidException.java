package stquokka.codeStudy.common.exception.User;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.UserErrorCode;

public class LanguageNotValidException extends BaseException {
    public LanguageNotValidException() {
        super(UserErrorCode.LANGUAGE_NOT_VALID);
    }
}
