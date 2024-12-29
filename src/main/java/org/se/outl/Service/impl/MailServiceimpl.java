package org.se.outl.Service.impl;


import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.se.outl.Mapper.MailMapper;
import org.se.outl.Mapper.UserMapper;
import org.se.outl.Service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;

@Service
public class MailServiceimpl implements MailService {

    @Autowired
    private MailMapper mailMapper;

    @Autowired
    private JavaMailSenderImpl javaMailSender;

    @Value("${spring.mail.username}")
    private String sendMailer;
    @Autowired
    private UserMapper userMapper;

    @Override
    public void sendTextMailMessage(String email,String subject,String text){

        try {
            //true 代表支持复杂的类型
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage(),true);
            //邮件发信人
            mimeMessageHelper.setFrom(sendMailer);
            //邮件收信人  1或多个
            mimeMessageHelper.setTo(email);
            //邮件主题
            mimeMessageHelper.setSubject(subject);
            //邮件内容
            mimeMessageHelper.setText(text);
            //邮件发送时间
            mimeMessageHelper.setSentDate(new Date());

            //发送邮件
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            System.out.println("发送邮件成功："+sendMailer+"->"+email);

        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("发送邮件失败："+e.getMessage());
        }
    }

    @Override
    public void addIdentity(String email,String code)
    {
        mailMapper.userAdd(email,code);
    }
}


