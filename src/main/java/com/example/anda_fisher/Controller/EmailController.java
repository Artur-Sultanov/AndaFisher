package com.example.anda_fisher.Controller;

import com.example.anda_fisher.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @GetMapping("/send-email")
    public String sendTestEmail(@RequestParam String to) {
        emailService.sendSimpleEmail(to, "Test Email", "This is a test email from Anda Fisher!");
        return "Email sent to: " + to;
    }
}
