package org.se.outl.Service.impl;


import jakarta.servlet.http.HttpSession;
import org.se.outl.Service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class MailServiceimpl implements MailService {


    @Autowired
    private JavaMailSenderImpl mailSender;

    // application.properties配置的值
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 给前端输入的邮箱，发送验证码
     *
     * @param email
     * @return
     */
    @Override
    public boolean sendMail(String email, HttpSession session) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            // 生成随机数
            String code = randomCode();

            // 将随机数放置到session中
            session.setAttribute("email", email);
            session.setAttribute("code", code);

            simpleMailMessage.setSubject("验证码邮件"); // 主题
            simpleMailMessage.setText("您收到的验证码是：" + code); // 内容
            simpleMailMessage.setFrom("2356304063@qq.com"); // 发件人
            simpleMailMessage.setTo(email); // 收件人
            mailSender.send(simpleMailMessage); // 发送

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 随机生成6位数的验证码
     *
     * @return String code
     */
    @Override
    public String randomCode() {
        StringBuilder str = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            str.append(random.nextInt(10));
        }
        return str.toString();
    }
}


