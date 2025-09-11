package dev.gagnon.DTO.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class UserApiResponse <T>{
    private boolean success;
    private T data;
}
