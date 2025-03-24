package com.gymfinder.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {
    @Autowired
    private  JavaMailSender mailSender;

    public String generateOTP() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(999999));
    }

    public void sendOTPEmail(String to, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(to);
        helper.setSubject("Your GymFinder Verification Code");

        String emailContent = String.format("""
            <html>
                <body>
                    <h2>Welcome to GymFinder!</h2>
                    <p>Your verification code is: <strong>%s</strong></p>
                    <p>This code will expire in 10 minutes.</p>
                    <p>If you didn't request this code, please ignore this email.</p>
                </body>
            </html>
            """, otp);

        helper.setText(emailContent, true);
        mailSender.send(message);
    }
}
