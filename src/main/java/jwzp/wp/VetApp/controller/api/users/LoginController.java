package jwzp.wp.VetApp.controller.api.users;

import com.nimbusds.jose.JOSEException;
import jwzp.wp.VetApp.models.dtos.UserLoginData;
import jwzp.wp.VetApp.service.Security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path="/api")
@RestController
public class LoginController {

    private JWTGenerator jwtGenerator;

    @Autowired
    public LoginController(JWTGenerator jwtGenerator) {
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginData user) throws JOSEException {
        var result = jwtGenerator.generateJWT(user);
        return result.isPresent()
                ? ResponseEntity.ok(result.get())
                : ResponseEntity.status(401).body("Incorrect login details");
    }
}
