package stquokka.codeStudy.common.exception.token;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.TokenErrorCode;

public class TokenTypeNotMatchedException extends BaseException {
    public TokenTypeNotMatchedException() {
        super(TokenErrorCode.NOT_MATCHED_TOKEN_TYPE);
    }
}
