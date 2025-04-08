package dev.gagnon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class BdicApiResponse <T>{
    private boolean isSuccessful;
    private T data;
}
