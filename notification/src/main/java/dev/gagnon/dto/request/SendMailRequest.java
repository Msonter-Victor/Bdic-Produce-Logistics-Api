package dev.gagnon.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SendMailRequest {
    private String to;
    private String name;
}
