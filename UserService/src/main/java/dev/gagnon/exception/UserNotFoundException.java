package dev.gagnon.exception;

public class UserNotFoundException extends UserBaseException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
