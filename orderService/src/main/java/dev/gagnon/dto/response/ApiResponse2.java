package dev.gagnon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse2<T> {
    private boolean success;
    private String message;
    private T data;
}
