package pap.project;

import jakarta.persistence.PersistenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pap.project.game_history.model.DataNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler
{
    @ExceptionHandler(DataNotFoundException.class)
    public @NonNull ResponseEntity<String> handleUserNotFound(@NonNull DataNotFoundException exception)
    {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public @NonNull ResponseEntity<String> handleException(@NonNull PersistenceException ex)
    {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
    }
}