package com.anghel.investmenthelper.auth.config;

import com.anghel.investmenthelper.auth.model.entity.AuthUser;
import com.anghel.investmenthelper.auth.repository.AuthUserRepository;
import com.anghel.investmenthelper.auth.util.enumeration.Role;
import com.anghel.investmenthelper.auth.util.property.BootStrapAdminProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class BootstrapAdminConfig {

    @Bean
    public CommandLineRunner bootstrapAdmin(
            AuthUserRepository authUserRepository,
            PasswordEncoder encoder,
            BootStrapAdminProperties properties) {
        return args -> {
            if (!properties.isEnabled()) {
                log.info("Bootstrap admin disabled");
                return;
            }

            AuthUser existingUser = authUserRepository.findAuthUserByEmail(properties.getEmail());
            if (existingUser != null) {
                log.info("Bootstrap admin already exists [email={}]", properties.getEmail());
                return;
            }

            AuthUser adminUser = new AuthUser();
            adminUser.setEmail(properties.getEmail());
            adminUser.setPasswordHash(encoder.encode(properties.getPassword()));
            adminUser.setRole(Role.ROLE_ADMIN);
            adminUser.setEnabled(true);

            authUserRepository.save(adminUser);
            log.info("Bootstrap admin created [email={}]", adminUser.getEmail());
        };
    }
}
