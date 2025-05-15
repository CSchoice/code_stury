package stquokka.codeStudy.common.exception.s3;

import stquokka.codeStudy.common.exception.BaseException;
import stquokka.codeStudy.common.exception.errorcode.S3ErrorCode;

public class ImageTooLargeException extends BaseException {
    public ImageTooLargeException() {
        super(S3ErrorCode.IMAGE_TOO_LARGE);
    }
}
