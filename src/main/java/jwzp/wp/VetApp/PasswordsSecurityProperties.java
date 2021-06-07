package jwzp.wp.VetApp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app.security")
public class PasswordsSecurityProperties {

    public final String PEPPER = "SDzxIGaB#64LAGc%EB89s6A*SV;f231";

}
