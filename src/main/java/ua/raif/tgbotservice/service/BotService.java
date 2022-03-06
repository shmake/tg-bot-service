package ua.raif.tgbotservice.service;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.raif.tgbotservice.config.TgBotProperties;
import ua.raif.tgbotservice.dao.UsersTelegramRepository;
import ua.raif.tgbotservice.domain.UsersTelegram;
import ua.raif.tgbotservice.service.sender.IBotSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class BotService extends TelegramLongPollingBot {

    @Autowired
    private TgBotProperties properties;

    @Autowired
    private IBotSender botSender;

    @Autowired
    private UsersTelegramRepository dao;

    @Override
    public String getBotUsername() {
        return properties.getName();
    }

    @Override
    public String getBotToken() {
        return properties.getToken();
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update updateMessage) {
        if (updateMessage.hasMessage()) {

            if(isNewUsersAdded(updateMessage)){
                var missed = updateMessage.getMessage().getNewChatMembers()
                        .stream()
                        .filter(c -> !c.getIsBot())
                        //.filter(c -> !checkableUserRepository.existsById(c.getId()))
//                        .filter(c -> {
//                            var ci = checkableUserRepository.findById(c.getId());
//                            return ci.isEmpty() || Objects.isNull(ci.get().isVerified());
//                        })
                        .map(this::mappingUserName)
                        .collect(Collectors.toList());
                botSender.sendHelloMessageToChatForVerify(missed);
            }

            if(isStartPrivateChat(updateMessage)){
                @NonNull var charId = updateMessage.getMessage().getChat().getId();
                @NonNull var userId = updateMessage.getMessage().getFrom().getId();
                botSender.sendRequestForContact(userId);
            }

            if(isMessageWithContact(updateMessage)) {

                var userId = updateMessage.getMessage().getContact().getUserId();
                var userName = updateMessage.getMessage().getContact().getFirstName();

                var phoneNumber = updateMessage.getMessage().getContact().getPhoneNumber();
                // validate phone number
                var user = new UsersTelegram();
                user.setPhoneNumber(phoneNumber);
                user.setUserName(userName);
                user.setUserId(userId);
                user.setVerified(Boolean.TRUE);
                dao.save(user);
//                var isValidPhone = false;
//                if (isValidPhone) {
//                    // set user valid DB
//                }else{
//                    //set to ban list
//                    var banChatMember = new BanChatMember();
//                    long chatId = -1001522161362L;
//                    banChatMember.setChatId(String.valueOf(chatId));
//                    banChatMember.setRevokeMessages(true);
//                    banChatMember.setUserId(userId);
//                    this.execute(banChatMember);
//                }
            }
        }
    }

    private boolean isStartPrivateChat(Update updateMessage) {
        @NonNull var typeChat = updateMessage.getMessage().getChat().getType();
        var text = updateMessage.getMessage().getText();
        if (Objects.nonNull(text) && text.equals("/start") && typeChat.equals("private")) {
            return true;
        }
        return false;
    }

    private String mappingUserName(User m) {
        if (m.getUserName() != null) {
            return m.getUserName();
        }

        return m.getFirstName() + " " + m.getLastName();
    }

    private boolean isNewUsersAdded(Update updateMessage) {
        return Objects.nonNull(updateMessage.getMessage().getNewChatMembers()) &&
                updateMessage.getMessage().getNewChatMembers().size() > 0;
    }

    private boolean isMessageWithContact(Update updateMessage) {
        return Objects.nonNull(updateMessage.getMessage().getContact());
    }

    private User getUser(Update updateMessage) {
        return updateMessage.getMessage().getFrom();
    }

    private boolean isNewAction(Update updateMessage) {
        return StringUtils.hasLength(updateMessage.getMessage().getText());
    }

}
