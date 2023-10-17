package mate.academy.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.time.LocalDateTime;
import java.util.List;
import mate.academy.exception.errors.BookApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        return buildResponseEntity(new BookApiError(BAD_REQUEST,
                LocalDateTime.now(), getErrorsMessage(ex.getBindingResult())));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    protected ResponseEntity<Object> handleEntityNotFoundException(
            EntityNotFoundException ex
    ) {
        return buildResponseEntity(new BookApiError(NOT_FOUND,
                LocalDateTime.now(), ex.getMessage()));
    }

    private ResponseEntity<Object> buildResponseEntity(BookApiError bookApiError) {
        return new ResponseEntity<>(bookApiError, bookApiError.status());
    }

    private List<String> getErrorsMessage(BindingResult bindingResult) {
        return bindingResult.getAllErrors().stream()
                .map(e -> ((FieldError) e).getField() + ": " + e.getDefaultMessage())
                .toList();
    }
}
