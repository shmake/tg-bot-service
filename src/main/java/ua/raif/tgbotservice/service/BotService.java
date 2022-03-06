package ua.raif.tgbotservice.service;

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
                        .map(User::getUserName)
                        .collect(Collectors.toList());
                botSender.sendHelloMessageToChatForVerify(missed);
            }

            if(isNewAction(updateMessage)){

                var user = getUser(updateMessage);
                // checkValidUser DB
                var message = sendMessageGetContact(updateMessage);

                this.execute(message);
            } else if (isMessageWithContact(updateMessage)) {
                var userId = updateMessage.getMessage().getContact().getUserId();
                var phoneNumber = updateMessage.getMessage().getContact().getPhoneNumber();
                // validate phone number
                var isValidPhone = false;
                if (isValidPhone) {
                    // set user valid DB
                }else{
                    //set to ban list
                    var banChatMember = new BanChatMember();
                    long chatId = -1001522161362L;
                    banChatMember.setChatId(String.valueOf(chatId));
                    banChatMember.setRevokeMessages(true);
                    banChatMember.setUserId(userId);
                    this.execute(banChatMember);
                }
            }
        }
    }

    private boolean isNewUsersAdded(Update updateMessage) {
        return Objects.nonNull(updateMessage.getMessage().getNewChatMembers());
    }

    private boolean isMessageWithContact(Update updateMessage) {
        return Objects.nonNull(updateMessage.getMessage().getContact());
    }

    private SendMessage sendMessageGetContact(Update updateMessage) {
        var message = new SendMessage(String.valueOf(updateMessage.getMessage().getFrom().getId()), "Please share you phone number");

        // create keyboard
        var replyKeyboardMarkup = new ReplyKeyboardMarkup();
        message.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);

        // new list
        List<KeyboardRow> keyboard = new ArrayList<>();

        // first keyboard line
        var keyboardFirstRow = new KeyboardRow();
        var keyboardButton = new KeyboardButton();
        keyboardButton.setText("Share your phone number >");
        keyboardButton.setRequestContact(true);
        keyboardFirstRow.add(keyboardButton);

        // add array to list
        keyboard.add(keyboardFirstRow);

        // add list to our keyboard
        replyKeyboardMarkup.setKeyboard(keyboard);
        return message;
    }

    private User getUser(Update updateMessage) {
        return updateMessage.getMessage().getFrom();
    }

    private boolean isNewAction(Update updateMessage) {
        return StringUtils.hasLength(updateMessage.getMessage().getText());
    }

}
