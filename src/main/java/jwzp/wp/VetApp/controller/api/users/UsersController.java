package jwzp.wp.VetApp.controller.api.users;

import com.nimbusds.jose.JOSEException;
import jwzp.wp.VetApp.controller.api.utils.ResponseToHttp;
import jwzp.wp.VetApp.models.dtos.UserRegisterData;
import jwzp.wp.VetApp.service.Security.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path="/api")
@RestController
public class UsersController {

    private UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }


    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody UserRegisterData user) throws JOSEException {
        var result = usersService.addUser(user);
        return result.succeed()
                ? ResponseEntity.ok("User was correctly added")
                : ResponseToHttp.getFailureResponse(result.getError());
    }
}
