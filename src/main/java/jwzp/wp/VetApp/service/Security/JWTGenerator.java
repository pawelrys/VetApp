package jwzp.wp.VetApp.service.Security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jwzp.wp.VetApp.models.dtos.UserData;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

public class JWTGenerator {

    private final static String secret = "PcIC0WidEfV4vIK4MmoQP9I8R0q4p3TJ";

    //Generowanie JWT na podstawie otrzymanych danych
    public static Optional<?> generateJWT(UserData user) throws JOSEException {
        JWSSigner signer = new MACSigner(secret.getBytes(StandardCharsets.UTF_8));
        //Sprawdzenie, czy dane logowania sie zgadzają
        //todo
        //Jeżeli nie:
        //return Optional.empty();

        //Zakładam, że odnalezione dane logowania należą do administratora
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                //todo uaktualnić dane pobrane z bazy
                .claim("login", user.getUsername())
                .claim("password", user.getPassword())
                .claim("role", "ROLE_ADMIN")
                //Ważność tokenu to 5 minut VV
                .expirationTime(new Date(new Date().getTime() + 300 * 1000))
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build(), jwtClaimsSet);
        signedJWT.sign(signer);

        return Optional.of(signedJWT.serialize());
    }
}
