package br.org.fadesp.reservasapi.exception;

public class ConflitoHorarioException extends RuntimeException {

    public ConflitoHorarioException(String message) {
        super(message);
    }
}