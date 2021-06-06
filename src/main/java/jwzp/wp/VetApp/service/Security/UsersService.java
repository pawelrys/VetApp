package jwzp.wp.VetApp.service.Security;

import jwzp.wp.VetApp.models.records.UserRecord;
import jwzp.wp.VetApp.resources.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;

public class UsersService {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository){
        this.usersRepository = usersRepository;
    }

    private String hashPassword(String password, String salt){

        return BCrypt.hashpw(password, salt);
    }

    private boolean isPasswordValid(String username, String password){
        UserRecord user = usersRepository.findByName(username);
        String hashedProvided = BCrypt.hashpw(password, user.getSalt());
        return hashedProvided.equals(user.getHashedPassword());
    }
}
