package stquokka.codeStudy.common.exception.s3;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.S3ErrorCode;

public class S3UploadFailedException extends BaseException {
    public S3UploadFailedException() {
        super(S3ErrorCode.S3_IMAGE_UPLOAD_FAILED);
    }
}
