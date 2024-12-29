package org.se.outl.Controller;


import lombok.extern.slf4j.Slf4j;
import org.se.outl.AllData.GlobalData;
import org.se.outl.Service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MailController {

    @Autowired
    private MailService mailService;

    @RequestMapping("/email/{email}")
    public void email(@PathVariable String email) {
        mailService.sendTextMailMessage(email,"certification code", GlobalData.getCode());
        mailService.addIdentity(email, GlobalData.getCode());
    }
}
