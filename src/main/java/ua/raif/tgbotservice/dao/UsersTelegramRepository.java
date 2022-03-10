package ua.raif.tgbotservice.dao;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import ua.raif.tgbotservice.domain.UsersTelegram;

import java.util.Optional;

@EnableScan
public interface UsersTelegramRepository extends CrudRepository<UsersTelegram, String> {
    Optional<UsersTelegram> findByUserId(String userId);

    boolean existsByUserIdAndIsVerified(String userId, Boolean isVerified);

}
