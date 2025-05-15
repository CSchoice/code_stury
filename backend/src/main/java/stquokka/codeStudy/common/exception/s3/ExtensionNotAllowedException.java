package stquokka.codeStudy.common.exception.s3;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.S3ErrorCode;

public class ExtensionNotAllowedException extends BaseException {
    public ExtensionNotAllowedException() {
        super(S3ErrorCode.EXTENSION_NOT_ALLOWED);
    }
}