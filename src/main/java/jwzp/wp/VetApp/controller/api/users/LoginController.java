package jwzp.wp.VetApp.controller.api.users;

import com.nimbusds.jose.JOSEException;
import jwzp.wp.VetApp.models.dtos.UserData;
import jwzp.wp.VetApp.service.Security.*;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(path="/api/login")
@RestController
public class LoginController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserData user) throws JOSEException {
        var result = JWTGenerator.generateJWT(user);
        return result.isPresent()
                ? ResponseEntity.ok(result.get())
                : ResponseEntity.status(401).body("Incorrect login details");
    }
}
