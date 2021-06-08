package jwzp.wp.VetApp.service.Security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component("userSecurity")
public class UserSecurity {
    public boolean hasUserId(Authentication authentication, Integer userId) {
        var collections = authentication.getAuthorities();
        var array = collections.toArray();
        return (int) array[1] == userId;
//        return true;
    }
}