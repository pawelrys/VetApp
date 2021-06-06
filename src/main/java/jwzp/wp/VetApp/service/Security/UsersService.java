package jwzp.wp.VetApp.service.Security;

import jwzp.wp.VetApp.models.dtos.UserData;
import jwzp.wp.VetApp.models.records.UserRecord;
import jwzp.wp.VetApp.resources.UsersRepository;
import jwzp.wp.VetApp.service.ErrorMessages.ResponseErrorMessage;
import jwzp.wp.VetApp.service.Response;
import jwzp.wp.VetApp.service.Utils.Checker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    private String hashPassword(String password, String salt) {

        return BCrypt.hashpw(password, salt);
    }

    private boolean isPasswordValid(String username, String password) {
        UserRecord user = usersRepository.findByName(username);
        String hashedProvided = BCrypt.hashpw(password, user.getSalt());
        return hashedProvided.equals(user.getHashedPassword());
    }

    private Response<UserRecord> addUser(UserData userDto) {
        Optional<ResponseErrorMessage> missingDataError = Checker.getMissingData(userDto);
        if (missingDataError.isPresent()){
            return Response.errorResponse(missingDataError.get());
        }
        String salt = BCrypt.gensalt();
        var user = UserRecord.createUserRecord(
                userDto.getUsername(),
                hashPassword(userDto.getPassword(), salt),
                salt
        );
        return Response.succeedResponse(user);
    }

}
