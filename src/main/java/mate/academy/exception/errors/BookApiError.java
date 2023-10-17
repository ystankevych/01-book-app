package mate.academy.exception.errors;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public record BookApiError(
        HttpStatus status,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
        LocalDateTime timestamp,
        Object errorMessages
) {}
