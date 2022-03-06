package ua.raif.tgbotservice.service.sender;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ua.raif.tgbotservice.config.TgBotProperties;

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
    public void sendMessage(String destination, String text){
        var message = new SendMessage(destination, text);
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
}
