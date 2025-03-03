package dev.gagnon.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BdicApiResponse<T>{
    private boolean  isSuccessful;
    private T  data;
}
