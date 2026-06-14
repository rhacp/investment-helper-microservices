package com.anghel.investmenthelper.auth.util.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.bootstrap-admin")
public class BootStrapAdminProperties {

    private boolean enabled;

    private String email;

    private String password;
}
