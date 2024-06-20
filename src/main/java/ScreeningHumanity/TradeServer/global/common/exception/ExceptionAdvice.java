package ScreeningHumanity.TradeServer.global.common.exception;

import ScreeningHumanity.TradeServer.global.common.response.BaseResponse;
import ScreeningHumanity.TradeServer.global.common.response.BaseResponseCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    /**
     * @return Exception Response 사용자 지정 (Custom Exception)을 응답합니다.
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> customException(CustomException e) {
        BaseResponse<Object> response = new BaseResponse<>(e.getStatus());
        return new ResponseEntity<>(response, response.httpStatus());
    }

    /**
     * @return Exception Response validation 관련 Error 사항을 응답합니다. 유효성 검증 실패. ex)@email 형식에 맞지 않음. 길이가
     * 맞지 않음. 등등.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> validationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("Constraint violation message not found");

        BaseResponse<?> response = new BaseResponse<>(
                BaseResponseCode.VALIDATION_FAIL_ERROR.getHttpStatus(),
                BaseResponseCode.VALIDATION_FAIL_ERROR.isSuccess(),
                message,
                BaseResponseCode.VALIDATION_FAIL_ERROR.getCode(),
                null);

        return new ResponseEntity<>(response, response.httpStatus());
    }

    /**
     * @return Exception Response validation 관련 Error 사항을 응답합니다. 유효성 검증 실패. ex)@email 형식에 맞지 않음. 길이가
     * 맞지 않음. 등등.
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<?> handlerValidationException(HandlerMethodValidationException e) {

        BaseResponse<?> response = new BaseResponse<>(
                BaseResponseCode.VALIDATION_FAIL_ERROR.getHttpStatus(),
                BaseResponseCode.VALIDATION_FAIL_ERROR.isSuccess(),
                e.getMessage(),
                BaseResponseCode.VALIDATION_FAIL_ERROR.getCode(),
                null);

        return new ResponseEntity<>(response, response.httpStatus());
    }


    /**
     * @return Client 잘못된 Method 입력
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> httpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {

        BaseResponse<?> response = new BaseResponse<>(
                BaseResponseCode.METHOD_NOT_ALLOW_ERROR.getHttpStatus(),
                BaseResponseCode.METHOD_NOT_ALLOW_ERROR.isSuccess(),
                e.getBody().getDetail(),
                BaseResponseCode.METHOD_NOT_ALLOW_ERROR.getCode(),
                null);

        return new ResponseEntity<>(response, response.httpStatus());
    }

    /**
     * @return 관련 Error 사항을 응답합니다. 잘못된 PathVariable 사용.
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<?> pathVariableException(MissingPathVariableException e) {
        BaseResponse<?> response = new BaseResponse<>(
                BaseResponseCode.PATH_VARIABLE_ERROR.getHttpStatus(),
                BaseResponseCode.PATH_VARIABLE_ERROR.isSuccess(),
                BaseResponseCode.PATH_VARIABLE_ERROR.getMessage(),
                BaseResponseCode.PATH_VARIABLE_ERROR.getCode(),
                null);

        return new ResponseEntity<>(response, response.httpStatus());
    }

    /**
     * @return 관련 Error 사항을 응답합니다. 잘못된 Request Parameter 사용.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> requestParameterException(MissingServletRequestParameterException e) {
        BaseResponse<?> response = new BaseResponse<>(
                BaseResponseCode.REQUEST_PARAM_ERROR.getHttpStatus(),
                BaseResponseCode.REQUEST_PARAM_ERROR.isSuccess(),
                BaseResponseCode.REQUEST_PARAM_ERROR.getMessage(),
                BaseResponseCode.REQUEST_PARAM_ERROR.getCode(),
                null);

        return new ResponseEntity<>(response, response.httpStatus());
    }

    /**
     * @return 관련 Error 사항을 응답합니다. 잘못된 Request Parameter 사용.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> noHandlerException(MissingServletRequestParameterException e) {
        BaseResponse<?> response = new BaseResponse<>(
                BaseResponseCode.NO_HANDLER_FOUND_ERROR.getHttpStatus(),
                BaseResponseCode.NO_HANDLER_FOUND_ERROR.isSuccess(),
                BaseResponseCode.NO_HANDLER_FOUND_ERROR.getMessage(),
                BaseResponseCode.NO_HANDLER_FOUND_ERROR.getCode(),
                null);

        return new ResponseEntity<>(response, response.httpStatus());
    }

    /**
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> noHandlerException(MethodArgumentNotValidException e) {

        String message = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();

        BaseResponse<?> response = new BaseResponse<>(
                BaseResponseCode.METHOD_NOT_ALLOW_ERROR.getHttpStatus(),
                BaseResponseCode.METHOD_NOT_ALLOW_ERROR.isSuccess(),
                message,
                BaseResponseCode.METHOD_NOT_ALLOW_ERROR.getCode(),
                null);

        return new ResponseEntity<>(response, response.httpStatus());
    }

    /**
     * @return 사전 처리 되지 못한 Exception 처리 사전에 custom exception 혹은, 공통 exception 처리 되지 못한 exception 처리.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> commonException(Exception e) {
        //로깅
        log.info("처리되지 않은 예외 발생. Message = {}", e.getMessage());
        BaseResponse<?> response = new BaseResponse<>(
                BaseResponseCode.INTERNAL_SERVER_ERROR.getHttpStatus(),
                BaseResponseCode.INTERNAL_SERVER_ERROR.isSuccess(),
                BaseResponseCode.INTERNAL_SERVER_ERROR.getMessage(),
                BaseResponseCode.INTERNAL_SERVER_ERROR.getCode(),
                null);

        return new ResponseEntity<>(response, response.httpStatus());
    }
}
