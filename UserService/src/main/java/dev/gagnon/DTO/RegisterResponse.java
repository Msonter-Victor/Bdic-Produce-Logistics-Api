package dev.gagnon.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
public class RegisterResponse {
    @JsonProperty("userId")
    private Long id;
    private String message;
    private String email;
    @JsonFormat(pattern = "dd-MMMM-yyyy 'at' hh:mm a")
    private LocalDateTime dateCreated;

}
