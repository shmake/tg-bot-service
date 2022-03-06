package ua.raif.tgbotservice.service.user;

import org.springframework.stereotype.Service;

@Service
public class UserService implements IUserService {
    @Override
    public boolean isPhoneNumberValid(String phoneNumber) {
        return false;
    }
}
