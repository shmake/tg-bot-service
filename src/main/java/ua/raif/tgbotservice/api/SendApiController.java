package ua.raif.tgbotservice.api;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.raif.tgbotservice.service.sender.BotSender;

@RestController
@RequestMapping("/send")
@RequiredArgsConstructor
public class SendApiController {

    private final BotSender botSender;

    @PostMapping
    public String sendMessageApi(@RequestBody SendMessageRequest request) {
        botSender.sendMessage(request.getName(), request.getMessage());
        return "ok";
    }

    @Value
    public static class SendMessageRequest{
        private String name;
        private String message;
    }
}


