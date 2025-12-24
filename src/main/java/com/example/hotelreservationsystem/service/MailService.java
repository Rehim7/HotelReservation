package com.example.hotelreservationsystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailService.class);
    private final JavaMailSender mailSender;
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public String sendMail(String to, String subject, String text) {
        logger.debug("HTML mail gönderme əməliyyatına başlanılır. Alıcı: {}", to);
        String htmlContent = generateSimpleHtmlContent(subject, text);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            logger.info("HTML mail uğurla göndərildi. Alıcı: '{}', Başlıq: '{}'", to, subject);

            return ("Mail uğurla göndərildi!");
        } catch (Exception e) {
            logger.error("Mail göndərilərkən xəta baş verdi. Alıcı: '{}'", to, e);
            return "Mail göndərilmədi: " + e.getMessage();
        }
    }
    private String generateSimpleHtmlContent(String subject, String text) {
        return "<html>"
                + "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>"
                + "<div style='background-color: #ffffff; padding: 20px; border-radius: 8px; border-left: 5px solid #007bff;'>"
                + "<h2 style='color: #333333;'>" + subject + "</h2>"
                + "<p style='color: #555555; line-height: 1.6;'>" + text + "</p>"
                + "<hr style='border: none; border-top: 1px solid #eeeeee; margin: 20px 0;'>"
                + "<p style='font-size: 12px; color: #aaaaaa;'>Hörmətlə,<br>Hotel Reservation System</p>"
                + "</div>"
                + "</body>"
                + "</html>";
    }

}