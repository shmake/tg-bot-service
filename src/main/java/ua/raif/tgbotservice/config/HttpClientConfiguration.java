package ua.raif.tgbotservice.config;

import org.apache.http.protocol.BasicHttpContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import ua.raif.tgbotservice.service.BotService;


@Configuration
public class HttpClientConfiguration {

//    @Bean
//    public BotService getBot(){
//        var defaultBotOptions = new DefaultBotOptions();
//        defaultBotOptions.setProxyType(DefaultBotOptions.ProxyType.HTTP);
//        defaultBotOptions.setProxyHost("rbaproxy.inet-dmz.kv.aval");
//        defaultBotOptions.setProxyPort(8000);
//        defaultBotOptions.setHttpContext(new BasicHttpContext());
//
//        return new BotService(defaultBotOptions);
//    }
}
