package dev.gagnon.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class LoginResponse {
    @JsonFormat(pattern = "dd-MMMM-yyyy 'at' hh:mm a")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime responseTime;
    private String message;
    private String firstName = "";
    private String lastName = "";
    private String email;
    private String phone;
    private String role;
    private String mediaUrl = "";
    private String token;
}
