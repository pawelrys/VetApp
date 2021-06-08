package jwzp.wp.VetApp.service.Security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jwzp.wp.VetApp.PasswordsSecurityProperties;
import jwzp.wp.VetApp.models.dtos.UserLoginData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
public class JWTGenerator {

    private final static String secret = "PcIC0WidEfV4vIK4MmoQP9I8R0q4p3TJ";

    private final UsersService usersService;
    private final String pepper;

    @Autowired
    public JWTGenerator(
            UsersService usersService,
            PasswordsSecurityProperties properties
    ) {
        this.usersService = usersService;
        this.pepper = properties.PEPPER;
    }

    //Generowanie JWT na podstawie otrzymanych danych
    public Optional<?> generateJWT(UserLoginData user) throws JOSEException {
        JWSSigner signer = new MACSigner(secret.getBytes(StandardCharsets.UTF_8));

        var userFromData = usersService.isPasswordValid(user.getUsername(), user.getPassword());
        if(userFromData.isEmpty()) return Optional.empty();

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .claim("login", userFromData.get().getUsername())
                .claim("role", userFromData.get().getRole().toString())
                .claim("id", userFromData.get().getId())
                //Ważność tokenu to 5 minut VV
                .expirationTime(new Date(new Date().getTime() + 300 * 1000))
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build(), jwtClaimsSet);
        signedJWT.sign(signer);

        return Optional.of(signedJWT.serialize());
    }
}
