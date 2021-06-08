package jwzp.wp.VetApp.service.Security;

import jwzp.wp.VetApp.models.dtos.UserLoginData;
import jwzp.wp.VetApp.models.dtos.UserRegisterData;
import jwzp.wp.VetApp.models.records.UserRecord;
import jwzp.wp.VetApp.models.values.Role;
import jwzp.wp.VetApp.resources.UsersRepository;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorMessagesBuilder;
import jwzp.wp.VetApp.service.ErrorMessages.ErrorType;
import jwzp.wp.VetApp.service.ErrorMessages.ResponseErrorMessage;
import jwzp.wp.VetApp.service.Response;
import jwzp.wp.VetApp.service.Utils.Checker;
import jwzp.wp.VetApp.PasswordsSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsersService {

    private final UsersRepository usersRepository;
    private final String pepper;

    @Autowired
    public UsersService(
            UsersRepository usersRepository,
            PasswordsSecurityProperties properties
            ) {
        this.usersRepository = usersRepository;
        this.pepper = properties.PEPPER;
        if(usersRepository.count() == 0) {
            addUser(new UserRegisterData("admin", "admin", Role.ROLE_ADMIN, -1));
        }
    }

    private String hashPassword(String password, String salt) {
        return BCrypt.hashpw(password, salt + pepper);
    }

    public Optional<UserRecord> isPasswordValid(String username, String password) {
        Optional<UserRecord> userOpt = usersRepository.findByName(username);
        if (userOpt.isEmpty()){
            return Optional.empty();
        }
        var user = userOpt.get();
        String hashedProvided = BCrypt.hashpw(password, user.getSalt() + pepper);
        return hashedProvided.equals(user.getHashedPassword())
                ? userOpt
                : Optional.empty();
    }

    public Response<UserRecord> addUser(UserRegisterData userDto) {
        Optional<ResponseErrorMessage> missingDataError = Checker.getMissingData(userDto);
        if (missingDataError.isPresent()){
            return Response.errorResponse(missingDataError.get());
        }

        if(usersRepository.findByName(userDto.getUsername()).isPresent()) {
            return Response.errorResponse(
                    ErrorMessagesBuilder.simpleError(
                            ErrorType.WRONG_ARGUMENTS,
                            "User with this name already exists"
                    ));
        }

        String salt = BCrypt.gensalt();
        var user = UserRecord.createUserRecord(
                userDto.getUsername(),
                hashPassword(userDto.getPassword(), salt),
                salt,
                userDto.getRole(),
                userDto.getConnectedRecordId()
        );

        usersRepository.save(user);

        return Response.succeedResponse(user);
    }

}
