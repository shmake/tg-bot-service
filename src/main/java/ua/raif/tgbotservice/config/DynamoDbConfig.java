package ua.raif.tgbotservice.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import org.socialsignin.spring.data.dynamodb.repository.config.EnableDynamoDBRepositories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDynamoDBRepositories(basePackages = "ua.raif.tgbotservice.dao")
public class DynamoDbConfig {

    @Bean
    public AmazonDynamoDB amazonDynamoDB(AWSSecretsProperties awsSecretsProperties) {
        return new AmazonDynamoDBClient(amazonAWSCredentials(awsSecretsProperties));
    }

    @Bean
    public AWSCredentials amazonAWSCredentials(AWSSecretsProperties awsSecretsProperties) {
        return new BasicAWSCredentials(
                awsSecretsProperties.getAccesskey(),
                awsSecretsProperties.getSecretkey());
    }
}
