package com.shxtnyra.forum.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {
    private String message;       // Сообщение об ошибке
    private String error;         // Тип ошибки (например, "FORBIDDEN")
    private int status;           // HTTP-статус (403, 404 и т.д.)
    private LocalDateTime timestamp; // Время возникновения ошибки
    private String path;          // URL, на котором произошла ошибка

    public ApiError(String message, String error, int status, String path) {
        this.message = message;
        this.error = error;
        this.status = status;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}
