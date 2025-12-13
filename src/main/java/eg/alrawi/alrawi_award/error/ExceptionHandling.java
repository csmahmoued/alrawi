package eg.alrawi.alrawi_award.error;

import eg.alrawi.alrawi_award.dto.ApiResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;

@Slf4j
@ControllerAdvice
public class ExceptionHandling extends ResponseEntityExceptionHandler {


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<?> handleVideoValidation(BusinessException ex) {
        return ResponseEntity.badRequest().body(ApiResponseDto.businessException(List.of(ex.getMessage())));
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> handleApiException(BaseException ex, WebRequest request) {
        log.info("ExceptionHandling: handleApiException {} ",request.getDescription(false));
        return new ResponseEntity<>(ApiResponseDto.error(List.of(ex.getMessage())), ex.getStatusCode());
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();
        return ResponseEntity.badRequest().body(ApiResponseDto.error(errors));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntime(RuntimeException ex) {
        log.error("handleRuntime {}",ex.getMessage(), ex);
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

}