package jwzp.wp.VetApp.service.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jwzp.wp.VetApp.models.values.Role;
import jwzp.wp.VetApp.resources.PetsRepository;
import jwzp.wp.VetApp.resources.VisitsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.stream.Collectors;

@Component("userSecurity")
public class UserSecurity {

    private final PetsRepository petsRepository;
    private final VisitsRepository visitsRepository;

    @Autowired
    public UserSecurity(PetsRepository petsRepository, VisitsRepository visitsRepository) {
        this.petsRepository = petsRepository;
        this.visitsRepository = visitsRepository;
    }

    public boolean checkUserId(Authentication authentication, Integer userId) {
        if(authentication.getCredentials().equals("")) return false;
        var claims = authentication.getAuthorities().toArray();
        if(claims[0].toString().equals(Role.ROLE_ADMIN.toString())) return true;
        return Integer.parseInt(authentication.getCredentials().toString()) == userId;
    }

    public boolean checkPostPet(Authentication authentication, HttpServletRequest request) throws ServletException, IOException {
        if(authentication.getCredentials().equals("")) return false;
        var claims = authentication.getAuthorities().toArray();
        if(claims[0].toString().equals(Role.ROLE_ADMIN.toString())) return true;

        var requestString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        var result = new ObjectMapper().readValue(requestString, HashMap.class);

        var idInRequest = (int) result.get("ownerId");
        var idInJWT = Integer.parseInt(authentication.getCredentials().toString());
        return idInJWT == idInRequest;
    }

    public boolean checkPetOwner(Authentication authentication, int petId) throws ServletException, IOException {
        if(authentication.getCredentials().equals("")) return false;
        var claims = authentication.getAuthorities().toArray();
        if(claims[0].toString().equals(Role.ROLE_ADMIN.toString())) return true;

        var pet = petsRepository.findById(petId);

        //If pet don't exist controller return message about it
        if (pet.isEmpty()) return true;
        return pet.get().owner.id == Integer.parseInt(authentication.getCredentials().toString());
    }

    public boolean checkAccessToVisit(Authentication authentication, int visitId) throws IOException {
        if(authentication.getCredentials().equals("")) return false;
        var claims = authentication.getAuthorities().toArray();
        if(claims[0].toString().equals(Role.ROLE_ADMIN.toString())) return true;

        var visit = visitsRepository.findById(visitId);

        //If visit don't exist controller return message about it
        if(visit.isEmpty()) return true;

        int jwtId = Integer.parseInt(authentication.getCredentials().toString());
        if(claims[0].toString().equals(Role.ROLE_VET.toString())) return jwtId == visit.get().vet.id;
        else {
            var pet = petsRepository.findById(visit.get().pet.id);

            //If pet don't exist controller return message about it
            if(pet.isEmpty()) return true;

            else{
                return pet.get().owner.id == jwtId;
            }
        }
    }

    public boolean checkAccessToVisit(Authentication authentication, HttpServletRequest request) throws IOException {
        if(authentication.getCredentials().equals("")) return false;
        var claims = authentication.getAuthorities().toArray();
        if(claims[0].toString().equals(Role.ROLE_ADMIN.toString())) return true;

        var requestString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        var result = new ObjectMapper().readValue(requestString, HashMap.class);

        int jwtId = Integer.parseInt(authentication.getCredentials().toString());
        if(claims[0].toString().equals(Role.ROLE_VET.toString())) return jwtId == (int) result.get("vetId");
        else {
            int petId = (int) result.get("petId");

            var pet = petsRepository.findById(petId);

            //If pet don't exist controller return message about it
            if(pet.isEmpty()) return true;

            else{
                return pet.get().owner.id == jwtId;
            }
        }
    }

    public boolean checkAccessToVisit(Authentication authentication, HttpServletRequest request, int visitId) throws IOException {
        if(authentication.getCredentials().equals("")) return false;
        var claims = authentication.getAuthorities().toArray();
        if(claims[0].toString().equals(Role.ROLE_ADMIN.toString())) return true;

        var visit = visitsRepository.findById(visitId);

        //If visit don't exist controller return message about it
        if(visit.isEmpty()) return true;

        int jwtId = Integer.parseInt(authentication.getCredentials().toString());
        if(claims[0].toString().equals(Role.ROLE_VET.toString())) return jwtId == visit.get().vet.id;
        else {
            var pet = petsRepository.findById(visit.get().pet.id);

            //If pet don't exist controller return message about it
            if(pet.isEmpty()) return true;

            else {
                if(pet.get().owner.id != jwtId) return false;
            }
        }

        var requestString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        var result = new ObjectMapper().readValue(requestString, HashMap.class);

        if(claims[0].toString().equals(Role.ROLE_VET.toString())) return jwtId == (int) result.get("vetId");
        else {
            int petId = (int) result.get("petId");

            var pet = petsRepository.findById(petId);

            //If pet don't exist controller return message about it
            if(pet.isEmpty()) return true;

            else{
                return pet.get().owner.id == jwtId;
            }
        }
    }
}