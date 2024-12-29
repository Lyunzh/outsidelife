package org.se.outl.Service;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpSession;

public interface MailService {
    void sendTextMailMessage(String email,String subject,String text);

    void addIdentity(String email,String code);
}
