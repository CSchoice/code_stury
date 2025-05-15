package stquokka.codeStudy.common.exception.security;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.SecurityErrorCode;

public class FilterException extends BaseException {
    public FilterException() {
        super(SecurityErrorCode.FILTER_ERROR);
    }
}
