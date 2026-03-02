package com.unito.client.service;
/*
Classe per gestire le eccezioni del tipo:
- server spento
- timeout
- risposta non valida (errore protocollo)
*/

public class ServiceException extends Exception {
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

}
