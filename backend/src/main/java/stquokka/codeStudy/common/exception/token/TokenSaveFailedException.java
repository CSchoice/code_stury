package stquokka.codeStudy.common.exception.token;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.TokenErrorCode;

public class TokenSaveFailedException extends BaseException {
        public TokenSaveFailedException() {
            super(TokenErrorCode.TOKEN_SAVE_FAILED);
        }
}
