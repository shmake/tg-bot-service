package ua.raif.tgbotservice.dao;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;
import ua.raif.tgbotservice.domain.PhonesUser;

import java.util.Optional;

@EnableScan
public interface PhonesUserRepository extends CrudRepository<PhonesUser, String> {
    Optional<PhonesUser> findByPhone(String phone);
}
