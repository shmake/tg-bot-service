package ua.raif.tgbotservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "amazon.aws")
public class AWSSecretsProperties {
    private String accesskey;
    private String secretkey;
}
