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

                .antMatchers("/api/login").permitAll()
                .antMatchers("/api/users").hasRole("ADMIN")


                .antMatchers("/api/clients").hasRole("ADMIN")
                .antMatchers("/api/clients/**").hasRole("ADMIN")

                .antMatchers( "/api/offices").hasRole("ADMIN")
                .antMatchers( "/api/offices/**").hasRole("ADMIN")

                .antMatchers("/api/vets").hasRole("ADMIN")
                .antMatchers("/api/vets/**").hasRole("ADMIN")



                .antMatchers(HttpMethod.GET, "/api/pets").hasAnyRole("ADMIN", "VET")
                .antMatchers(HttpMethod.GET, "/api/pets/client/{clientId}").access("@userSecurity.checkUserId(authentication, #clientId)")
                .antMatchers(HttpMethod.GET, "/api/pets/pet/{petId}").access("@userSecurity.checkPetOwner(authentication, #petId)")
                .antMatchers(HttpMethod.POST, "/api/pets").access("@userSecurity.checkPostPet(authentication, request)")
                .antMatchers(HttpMethod.PATCH, "/api/pets/{petId}").access("@userSecurity.checkPetOwner(authentication, #petId)")
                .antMatchers(HttpMethod.DELETE, "/api/pets/{petId}").access("@userSecurity.checkPetOwner(authentication, #petId)")


                .antMatchers(HttpMethod.GET, "/api/visits").hasAnyRole("ADMIN", "VET")
                .antMatchers(HttpMethod.GET, "/api/visits/{clientId}").access("@userSecurity.checkUserId(authentication, #clientId)")
                .antMatchers(HttpMethod.GET, "/api/visits/{vetId}").access("@userSecurity.checkUserId(authentication, #vetId)")
                .antMatchers(HttpMethod.GET, "/api/visits/{visitId}").access("@userSecurity.checkAccessToVisit(authentication, #visitId)")
                .antMatchers(HttpMethod.PATCH, "/api/visits/{visitId}").access("@userSecurity.checkAccessToVisit(authentication, request, #visitId)")
                .antMatchers(HttpMethod.POST, "/api/visits").access("@userSecurity.checkAccessToVisit(authentication, request)")
                .antMatchers(HttpMethod.DELETE, "/api/visits/{visitId}").access("@userSecurity.checkAccessToVisit(authentication, #visitId)")
                .antMatchers(HttpMethod.GET, "/api/visits/available-time-slots").hasAnyRole("CLIENT", "VET", "ADMIN")

                .anyRequest().permitAll()
                .and()
                .addFilter(new JWTFilter(authenticationManager()));
    }

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/api/login");
//    }
}
