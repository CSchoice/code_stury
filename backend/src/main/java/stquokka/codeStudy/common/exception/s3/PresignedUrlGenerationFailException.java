package stquokka.codeStudy.common.exception.s3;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.S3ErrorCode;

public class PresignedUrlGenerationFailException extends BaseException {
    public PresignedUrlGenerationFailException() {
        super(S3ErrorCode.PERSIGNEDURL_GENERATION_FAILED);
    }
}
