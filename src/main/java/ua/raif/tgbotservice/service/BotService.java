package ua.raif.tgbotservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.raif.tgbotservice.config.TgBotProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class BotService extends TelegramLongPollingBot {

    @Autowired
    private TgBotProperties properties;

    @Override
    public String getBotUsername() {
        return properties.getName();
    }

    @Override
    public String getBotToken() {
        return properties.getToken();
    }

    @Override
    public void onUpdateReceived(Update updateMessage) {
        LOG.info("" + updateMessage.getUpdateId());
        if (updateMessage.hasMessage()) {

            if(isNewMassage(updateMessage)){

                var user = getUser(updateMessage);


                SendMessage message = new SendMessage(String.valueOf(updateMessage.getMessage().getFrom().getId()), "You need verify");

                // create keyboard
                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
                message.setReplyMarkup(replyKeyboardMarkup);
                replyKeyboardMarkup.setSelective(true);
                replyKeyboardMarkup.setResizeKeyboard(true);
                replyKeyboardMarkup.setOneTimeKeyboard(true);

                // new list
                List<KeyboardRow> keyboard = new ArrayList<>();

                // first keyboard line
                KeyboardRow keyboardFirstRow = new KeyboardRow();
                KeyboardButton keyboardButton = new KeyboardButton();
                keyboardButton.setText("Share your number >");
                keyboardButton.setRequestContact(true);
                keyboardFirstRow.add(keyboardButton);

                // add array to list
                keyboard.add(keyboardFirstRow);

                // add list to our keyboard
                replyKeyboardMarkup.setKeyboard(keyboard);
                try {
                    this.execute(message);
                } catch (TelegramApiException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
    }

    private User getUser(Update updateMessage) {
        return updateMessage.getMessage().getFrom();
    }

    private boolean isNewMassage(Update updateMessage) {
        return StringUtils.hasLength(updateMessage.getMessage().getText());
    }

}
