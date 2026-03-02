package com.unito.client.service;

public interface MailService {
    boolean checkUserExists(String email) throws ServiceException;
}
