package jwzp.wp.VetApp.service.Security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Component("userSecurity")
public class UserSecurity {
    public boolean hasUserId(Authentication authentication, Integer userId) {
        var collections = authentication.getAuthorities();
        var array = collections.toArray();
        return (int) array[1] == userId;
    }

    public boolean check(Authentication authentication, HttpServletRequest request) throws ServletException, IOException {

        var test = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        var test1 = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        var test12 = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        return true;
    }
}