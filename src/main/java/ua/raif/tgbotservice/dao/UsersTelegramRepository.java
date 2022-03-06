package ua.raif.tgbotservice.dao;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import ua.raif.tgbotservice.domain.UsersTelegram;

@EnableScan
public interface UsersTelegramRepository extends CrudRepository<UsersTelegram, String> {
}
