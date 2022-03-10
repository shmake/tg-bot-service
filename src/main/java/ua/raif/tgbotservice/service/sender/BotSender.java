package ua.raif.tgbotservice.service.sender;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.Days;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.raif.tgbotservice.config.TgBotProperties;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BotSender extends DefaultAbsSender implements IBotSender {

    private final TgBotProperties prop;

    public BotSender(DefaultBotOptions options, TgBotProperties prop) {
        super(options);
        this.prop = prop;
    }

    @Override
    public String getBotToken() {
        return prop.getToken();
    }

    @SneakyThrows
    @Override
    public void bannedUserInChat(Long userId) {
        var banChatMember = new BanChatMember();
        banChatMember.setUserId(userId);
        banChatMember.setRevokeMessages(true);
        banChatMember.setChatId(prop.getChatId());
        banChatMember.setUntilDateInstant(ZonedDateTime.now().minusDays(1).toInstant());

        this.execute(banChatMember);
    }

    @SneakyThrows
    @Override
    public void sendSuccessValidationMessage(Long userId) {
        var message = new SendMessage(String.valueOf(userId), "Your phone is valid");
        this.execute(message);
    }

    @SneakyThrows
    @Override
    public void sendNonValidPhoneMessage(Long userId) {
        var message = new SendMessage(String.valueOf(userId), "Sorry, your phone is non valid");
        this.execute(message);
    }

    @SneakyThrows
    @Override
    public void sendHelloMessageToChatForVerify(List<String> users) {
        var collect = users.stream()
                .map(m -> "@" + m)
                .collect(Collectors.joining(","));
        var message = new SendMessage(prop.getChatId(), "Hi, please start in @" + prop.getName() + ": " + collect);
        this.execute(message);
    }

    @SneakyThrows
    @Override
    public void sendRequestForContact(Long userId) {
        var message = new SendMessage(String.valueOf(userId), "Please share you phone number");

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
        this.execute(message);
    }

    @Override
    public void removeAllMessagesForNonValidUser(Long userId, List<String> messages) {
        for (String msg: messages) {
            var deleteMessage = new DeleteMessage();
            deleteMessage.setMessageId(Integer.valueOf(msg));
            deleteMessage.setChatId(prop.getChatId());
            try {
                this.execute(deleteMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
