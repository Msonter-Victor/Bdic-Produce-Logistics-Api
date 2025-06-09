package dev.gagnon.exceptions;

public class CompanyExistsException extends LogisticsBaseException{
    public CompanyExistsException(String message) {
        super(message);
    }
}
