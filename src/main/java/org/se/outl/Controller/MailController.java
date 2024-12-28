package org.se.outl.Controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class MailController {
    @RequestMapping("/email/{email}")
    public String email(@PathVariable String email) {

    }
}
