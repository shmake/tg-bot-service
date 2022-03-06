package ua.raif.tgbotservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "tg.bot")
public class TgBotProperties {

    private String name;

    private String token;

    private final String chatId = "-1001522161362L";
}
