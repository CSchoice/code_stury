package stquokka.codeStudy.common.exception.errorcode;

public interface BaseErrorCode {
    String getCode();
    String getMessage();
    Integer getHttpStatus();
}
