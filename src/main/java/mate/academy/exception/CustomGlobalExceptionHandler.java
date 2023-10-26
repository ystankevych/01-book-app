package mate.academy.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> invalidArgument(MethodArgumentNotValidException ex) {
        return buildResponseEntity(new BookApiErrorResponse(BAD_REQUEST,
                LocalDateTime.now(), getErrorsMessage(ex.getBindingResult())));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
        return buildResponseEntity(new BookApiErrorResponse(NOT_FOUND,
                LocalDateTime.now(), ex.getMessage()));
    }

    @ExceptionHandler(RegistrationException.class)
    protected ResponseEntity<Object> handleRegistrationException(RegistrationException ex) {
        return buildResponseEntity(new BookApiErrorResponse(BAD_REQUEST,
                LocalDateTime.now(), ex.getMessage()));
    }

    private ResponseEntity<Object> buildResponseEntity(BookApiErrorResponse bookApiError) {
        return new ResponseEntity<>(bookApiError, bookApiError.status());
    }

    private Map<String, String> getErrorsMessage(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .filter(e -> e instanceof FieldError)
                .collect(Collectors.groupingBy(e -> ((FieldError)e).getField(),
                        Collectors.mapping(DefaultMessageSourceResolvable::getDefaultMessage,
                                Collectors.joining())));
    }
}
