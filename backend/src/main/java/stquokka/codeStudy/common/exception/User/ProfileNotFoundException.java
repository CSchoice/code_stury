package stquokka.codeStudy.common.exception.User;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.UserErrorCode;

public class ProfileNotFoundException extends BaseException {
    public ProfileNotFoundException() {
        super(UserErrorCode.PROFILE_NOT_FOUND);
    }
}