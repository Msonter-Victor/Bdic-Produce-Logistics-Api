package dev.gagnon.DTO.response;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomResponse<T> {
    private boolean success;
    private String message;
    private T data;
}
