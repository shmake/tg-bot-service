package ua.raif.tgbotservice.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.raif.tgbotservice.dao.PhonesUserRepository;
import ua.raif.tgbotservice.domain.PhonesUser;

@RestController
@RequestMapping("/phone")
public class ApiController {

    @Autowired
    private PhonesUserRepository dao;

    @PostMapping("/{phone}")
    public String savePhone(@PathVariable String phone) {
        var phonesUser = new PhonesUser();
        phonesUser.setPhone(phone);
        dao.save(phonesUser);
        return "ok";
    }
}
