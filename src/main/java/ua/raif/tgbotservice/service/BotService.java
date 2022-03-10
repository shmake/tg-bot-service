package ua.raif.tgbotservice.service;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ua.raif.tgbotservice.config.TgBotProperties;
import ua.raif.tgbotservice.dao.PhonesUserRepository;
import ua.raif.tgbotservice.dao.UsersTelegramRepository;
import ua.raif.tgbotservice.domain.UsersTelegram;
import ua.raif.tgbotservice.exception.NonValidUserException;
import ua.raif.tgbotservice.exception.NotFoundUserBotException;
import ua.raif.tgbotservice.service.sender.IBotSender;

import java.util.*;
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

    @Autowired
    private PhonesUserRepository daoPhones;

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

            if (isNewUsersAdded(updateMessage)) {
                var listUsers = updateMessage.getMessage().getNewChatMembers()
                        .stream()
                        .filter(c -> !c.getIsBot())
                        .map(this::createNewUser)
                        //.filter(c -> !checkableUserRepository.existsById(c.getId()))
//                        .filter(c -> {
//                            var ci = checkableUserRepository.findById(c.getId());
//                            return ci.isEmpty() || Objects.isNull(ci.get().isVerified());
//                        })
                        .map(this::mappingUserName)
                        .collect(Collectors.toList());
                botSender.sendHelloMessageToChatForVerify(listUsers);
            }

            if (isStartPrivateChat(updateMessage)) {
                @NonNull var charId = updateMessage.getMessage().getChat().getId();
                @NonNull var userId = updateMessage.getMessage().getFrom().getId();
                botSender.sendRequestForContact(userId);
            }

            if (isMessageWithContact(updateMessage)) {
                try {
                    var userId = updateMessage.getMessage().getContact().getUserId();
                    var phoneNumber = updateMessage.getMessage().getContact().getPhoneNumber();
                    // validate phone number
                    validatePhoneNumber(phoneNumber, userId);
                    botSender.sendSuccessValidationMessage(userId);
                } catch (NonValidUserException ex) {
                    var userId = updateMessage.getMessage().getContact().getUserId();
                    removeAllMessage(userId);
                    botSender.sendNonValidPhoneMessage(userId);
                    botSender.bannedUserInChat(userId);
                }
            }

            if (isNewAction(updateMessage)) {
                var user = updateMessage.getMessage().getFrom();
                var isVerified = dao.existsByUserIdAndIsVerified(String.valueOf(user.getId()), true);
                if (!isVerified) {
                    var byUserId = dao.findByUserId(String.valueOf(user.getId()));

                    byUserId.map(u -> {
                        var messages = u.getMessages();
                        if (Objects.isNull(messages)) {
                            var initMessage = new ArrayList<String>();
                            initMessage.add(String.valueOf(updateMessage.getMessage().getMessageId()));
                            u.setMessages(initMessage);
                        } else {
                            messages.add(String.valueOf(updateMessage.getMessage().getMessageId()));
                        }
                        return dao.save(u);
                    }).orElseGet(() -> createNewUserMessage(user, String.valueOf(updateMessage.getMessage().getMessageId())));
                }
            }
        }
    }

    private void removeAllMessage(Long userId) {
        var byUserId = dao.findByUserId(String.valueOf(userId));
        var messages = byUserId.map(UsersTelegram::getMessages).orElseGet(Collections::emptyList);
        botSender.removeAllMessagesForNonValidUser(userId, messages);
    }

    private UsersTelegram createNewUserMessage(User m, String message) {
        UsersTelegram usersTelegram = getUsersTelegram(m);
        usersTelegram.setMessages(List.of(message));
        return dao.save(usersTelegram);
    }

    private User createNewUser(User m) {
        UsersTelegram usersTelegram = getUsersTelegram(m);
        dao.save(usersTelegram);
        return m;
    }

    private UsersTelegram getUsersTelegram(User m) {
        var usersTelegram = new UsersTelegram();
        usersTelegram.setUserId(m.getId());
        usersTelegram.setVerified(Boolean.FALSE);
        usersTelegram.setUserName(m.getUserName());
        usersTelegram.setFirstName(m.getFirstName());
        usersTelegram.setLastName(m.getLastName());
        return usersTelegram;
    }

    private void validatePhoneNumber(String phoneNumber, Long userId) {
        var userByPhones = daoPhones.findByPhone(phoneNumber);
        userByPhones.ifPresentOrElse(e -> {
            var byUserId = dao.findByUserId(String.valueOf(userId));
            byUserId.map(u -> {
                u.setPhoneNumber(phoneNumber);
                u.setVerified(Boolean.TRUE);
                return dao.save(u);
            }).orElseThrow(() -> new NotFoundUserBotException("" + userId));
        }, () -> {
            throw new NonValidUserException("Not found User with phone: " + phoneNumber);
        });
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

    private boolean isNewAction(Update updateMessage) {
        return StringUtils.hasLength(updateMessage.getMessage().getText());
    }

}
