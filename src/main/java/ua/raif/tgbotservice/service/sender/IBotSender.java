package ua.raif.tgbotservice.service.sender;

import java.util.List;

public interface IBotSender {

    void bannedUserInChat(Long userId);

    void sendSuccessValidationMessage(Long userId);

    void sendNonValidPhoneMessage(Long userId);

    void sendHelloMessageToChatForVerify(List<String> users);

    void sendRequestForContact(Long userId);

    void removeAllMessagesForNonValidUser(Long userId, List<String> messages);
}
