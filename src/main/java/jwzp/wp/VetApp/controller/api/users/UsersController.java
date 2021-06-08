package jwzp.wp.VetApp.controller.api.users;

import com.nimbusds.jose.JOSEException;
import jwzp.wp.VetApp.controller.api.utils.ResponseToHttp;
import jwzp.wp.VetApp.models.dtos.UserData;
import jwzp.wp.VetApp.models.values.Role;
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
    @ResponseBody
    public ResponseEntity<?> addUser(@RequestParam Role role, @RequestParam int id, @RequestBody UserData user) throws JOSEException {
        var result = usersService.addUser(user, role, id);
        return result.succeed()
                ? ResponseEntity.ok("todo") //todo
                : ResponseToHttp.getFailureResponse(result.getError());
    }
}
