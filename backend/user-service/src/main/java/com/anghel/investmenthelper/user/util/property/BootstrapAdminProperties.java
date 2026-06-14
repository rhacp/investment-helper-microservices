package com.anghel.investmenthelper.user.util.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.LocalDate;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.bootstrap-admin")
public class BootstrapAdminProperties {

    private boolean enabled;

    private Long authUserId;

    private String firstName;

    private String lastName;

    private LocalDate dateOfBirth;
}
