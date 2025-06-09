package dev.gagnon.exception;

public class ResourceNotFoundException extends OrderBaseException{
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
