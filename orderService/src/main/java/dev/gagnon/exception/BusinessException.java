package dev.gagnon.exception;

public class BusinessException extends OrderBaseException{
    public BusinessException(String message) {
        super(message);
    }
}
