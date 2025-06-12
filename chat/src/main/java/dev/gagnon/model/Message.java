package dev.gagnon.Model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Message {

    private String senderName;

    private String receiverName;

    private String message;

    private String date;

    private Status status;
}