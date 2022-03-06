package ua.raif.tgbotservice.service.sender;

import java.util.List;

public interface IBotSender {

    void sendHelloMessageToChatForVerify(List<String> users);

    void sendRequestForContact(Long userId);
}
