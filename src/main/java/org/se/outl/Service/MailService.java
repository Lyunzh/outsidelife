package org.se.outl.Service;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.servlet.http.HttpSession;

public interface MailService {
    public boolean sendMail( String email, HttpSession session);
    public String randomCode();
}
