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
                .antMatchers(HttpMethod.POST, "/api/clients").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/api/clients/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/clients/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/offices").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/api/offices/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/offices/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/api/vets").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/api/vets/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/vets/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/api/visits/**").hasAnyRole("CLIENT", "VET", "ADMIN")
                .antMatchers(HttpMethod.POST, "/api/visits").hasAnyRole("CLIENT", "VET", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/visits/**").hasAnyRole("CLIENT", "VET", "ADMIN")
                .antMatchers(HttpMethod.POST, "/api/pets").hasAnyRole("CLIENT", "ADMIN")
                .antMatchers(HttpMethod.PATCH, "/api/pets/**").hasAnyRole("CLIENT", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/pets/**").hasAnyRole("CLIENT", "ADMIN")
                .anyRequest().permitAll()
                .and()
                .addFilter(new JWTFilter(authenticationManager()));
    }
}
