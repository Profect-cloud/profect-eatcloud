package profect.eatcloud.login.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import profect.eatcloud.login.service.MailService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MailTestController {

    private final MailService mailService;

    @GetMapping("/test-email")
    public String testEmailSend() {
        mailService.sendMail("919jung@gmail.com", "메일 발송 테스트", "내용입니다.");
        return "이메일 전송 완료!";
    }
}
