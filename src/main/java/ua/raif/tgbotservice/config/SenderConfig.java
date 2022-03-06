package ua.raif.tgbotservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import ua.raif.tgbotservice.service.sender.BotSender;

@Configuration
public class SenderConfig {

    @Bean
    public BotSender getBotSender(TgBotProperties properties) {
        var defaultBotOptions = new DefaultBotOptions();
        return new BotSender(defaultBotOptions, properties);
    }
}
