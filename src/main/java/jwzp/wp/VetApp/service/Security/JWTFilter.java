package jwzp.wp.VetApp.service.Security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Set;

public class JWTFilter extends BasicAuthenticationFilter {

    private final static String secret = "PcIC0WidEfV4vIK4MmoQP9I8R0q4p3TJ";


    public JWTFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest = new CachedBodyHttpServletRequest(request);
        String header = request.getHeader("Authorization");
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = null;

        String jwt;

        if (header != null && header.startsWith("Bearer ")) {
            jwt = header.substring(7);
            SignedJWT signedJWT = null;
            try {
                signedJWT = SignedJWT.parse(jwt);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
            }

            if (isSignatureValid(jwt) && signedJWT != null) {
                String username = (String) signedJWT.getPayload().toJSONObject().get("login");
                String role = (String) signedJWT.getPayload().toJSONObject().get("role");
                String id =  String.valueOf(signedJWT.getPayload().toJSONObject().get("id"));

                Set<SimpleGrantedAuthority> simpleGrantedAuthorities = Set.of(new SimpleGrantedAuthority(role));
                usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(username, id, simpleGrantedAuthorities);
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(cachedBodyHttpServletRequest));
            }
        }
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        chain.doFilter(cachedBodyHttpServletRequest, response);
    }

    //Sprawdzanie poprawności JWT
    public boolean isSignatureValid(String token) {
        SignedJWT signedJWT;
        try {
            signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new MACVerifier(secret.getBytes());
            if (signedJWT.verify(verifier)) {
                Date referenceTime = new Date();
                JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
                Date expirationTime = claims.getExpirationTime();
                boolean expired = expirationTime == null || expirationTime.before(referenceTime);
                return !expired;
            }
        } catch (JOSEException | ParseException e) {
            return false;
        }
        return false;
    }
}
