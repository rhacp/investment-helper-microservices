package com.anghel.investmenthelper.user.config;

import com.anghel.investmenthelper.user.model.entity.User;
import com.anghel.investmenthelper.user.repository.UserRepository;
import com.anghel.investmenthelper.user.util.property.BootstrapAdminProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class BootstrapAdminProfileConfig {

    @Bean
    public CommandLineRunner bootstrapAdminProfile(
            UserRepository userRepository,
            BootstrapAdminProperties properties) {

        return args -> {
            if (!properties.isEnabled()) {
                log.info("Bootstrap admin profile disabled");
                return;
            }

            User existingUser = userRepository.findUserByAuthUserId(properties.getAuthUserId());

            if (existingUser != null) {
                log.info("Bootstrap admin profile already exists [authUserId={}]",
                        properties.getAuthUserId());
                return;
            }

            User user = new User();
            user.setAuthUserId(properties.getAuthUserId());
            user.setFirstName(properties.getFirstName());
            user.setLastName(properties.getLastName());
            user.setDateOfBirth(properties.getDateOfBirth());

            userRepository.save(user);

            log.info("Bootstrap admin profile created [authUserId={}]",
                    user.getAuthUserId());
        };
    }
}
