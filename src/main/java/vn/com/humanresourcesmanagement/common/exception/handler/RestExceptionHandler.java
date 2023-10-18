package vn.com.humanresourcesmanagement.common.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import vn.com.humanresourcesmanagement.common.constants.Constant;
import vn.com.humanresourcesmanagement.common.exception.AuthenticationException;
import vn.com.humanresourcesmanagement.common.exception.BusinessException;
import vn.com.humanresourcesmanagement.common.exception.model.ExceptionModel;
import vn.com.humanresourcesmanagement.common.utils.StringUtils;

import java.io.Serializable;
import java.util.Objects;

@RestControllerAdvice()
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RestExceptionHandler extends Throwable implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

    private static final long serialVersionUID = 1L;

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @Order()
    public ResponseEntity<ExceptionModel> handleAllException(Throwable ex) {
        var exceptionDTO = ExceptionModel.builder().message(Constant.EXCEPTION_MESSAGE_DEFAULT).description(ex.getLocalizedMessage()).build();
        return new ResponseEntity<>(exceptionDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public ResponseEntity<ExceptionModel> handleBusinessException(BusinessException ex) {
        var message = Objects.isNull(ex) || StringUtils.isNullOrEmpty(ex.getMessage()) ? Constant.EXCEPTION_MESSAGE_DEFAULT : ex.getMessage();
        var code = Objects.isNull(ex) || StringUtils.isNullOrEmpty(ex.getCode()) ? null : ex.getCode();
        var details = Objects.isNull(ex) ? null : ex.getData();
        var exceptionDTO = ExceptionModel.builder().message(message).code(code).detail(details).build();
        return new ResponseEntity<>(exceptionDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @Order(value = Ordered.HIGHEST_PRECEDENCE)
    public ResponseEntity<ExceptionModel> handleAuthenticationException(AuthenticationException ex) {
        var message = Objects.isNull(ex) || StringUtils.isNullOrEmpty(ex.getMessage()) ? "Thông tin truy cập không hợp lệ, vui lòng đăng nhập lại." : ex.getMessage();
        var httpStatus = Objects.isNull(ex) || Objects.isNull(ex.getStatusCode()) ? HttpStatus.UNAUTHORIZED : HttpStatus.valueOf(ex.getStatusCode());
        var exceptionDTO = ExceptionModel.builder().message(message).build();
        return new ResponseEntity<>(exceptionDTO, httpStatus);
    }
}
