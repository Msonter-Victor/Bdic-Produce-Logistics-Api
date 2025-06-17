package dev.gagnon.exceptions;

public class ResourceNotFoundException extends LogisticsBaseException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
