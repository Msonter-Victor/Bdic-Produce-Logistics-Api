package dev.gagnon.exception;

public class EmailExistsException extends BdicBaseException{
    public EmailExistsException(String message) {
        super(message);
    }
}
