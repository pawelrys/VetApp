package jwzp.wp.VetApp.service.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().and().csrf().disable().authorizeRequests()

                .antMatchers(HttpMethod.POST, "/api/login").permitAll()
                .antMatchers("/api/users").hasRole("ADMIN")


                .antMatchers("/api/clients").hasRole("ADMIN")
                .antMatchers("/api/clients/**").hasRole("ADMIN")

                .antMatchers( "/api/offices").hasRole("ADMIN")
                .antMatchers( "/api/offices/**").hasRole("ADMIN")

                .antMatchers("/api/vets").hasRole("ADMIN")
                .antMatchers("/api/vets/**").hasRole("ADMIN")



                .antMatchers(HttpMethod.GET, "/api/users/pets").hasAnyRole("ADMIN", "VET")
//                .antMatchers(HttpMethod.GET, "/api/users/{clientId}/pets").access("@userSecurity.hasUserId(authentication,#clientId)")
                .antMatchers(HttpMethod.GET, "/api/users/{clientId}/pets").access("@userSecurity.check(authentication, request)")
                .antMatchers(HttpMethod.GET, "/api/users/{clientId}/pets/{petId}").access("@userSecurity.hasUserId(authentication,#clientId)")
                .antMatchers(HttpMethod.POST, "/api/users/{clientId}/pets").access("@userSecurity.hasUserId(authentication,#clientId)")
                .antMatchers(HttpMethod.PATCH, "/api/users/{clientId}/pets/{petId}").access("@userSecurity.hasUserId(authentication,#clientId)")
                .antMatchers(HttpMethod.DELETE, "/api/users/{clientId}/pets/{petId}").access("@userSecurity.hasUserId(authentication,#clientId)")



                .antMatchers(HttpMethod.PATCH, "/api/visits/**").hasAnyRole("CLIENT", "VET", "ADMIN")
                .antMatchers(HttpMethod.POST, "/api/visits").hasAnyRole("CLIENT", "VET", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/visits/**").hasAnyRole("CLIENT", "VET", "ADMIN")

                .anyRequest().permitAll()
                .and()
                .addFilter(new JWTFilter(authenticationManager()));
    }
}
