package stquokka.codeStudy.common.exception.security;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.SecurityErrorCode;

public class PasswordUsedException extends BaseException {
    public PasswordUsedException() {super (SecurityErrorCode.PASSWORD_USED);}

}
