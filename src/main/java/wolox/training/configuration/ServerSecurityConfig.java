package wolox.training.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import wolox.training.services.UserAuthenticationProvider;

@Configuration
@EnableWebSecurity
@ComponentScan("wolox.training.services")
public class ServerSecurityConfig extends WebSecurityConfigurerAdapter {

    public static String encodePassword(String plainPassword) {
        return new BCryptPasswordEncoder().encode(plainPassword);
    }

    public static boolean isPasswordValid(String plainPassword, String encodedPassword) {
        return  new BCryptPasswordEncoder().matches(plainPassword, encodedPassword);
    }

    @Autowired
    private UserAuthenticationProvider authProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.POST, "/api/books")
            .and().ignoring().antMatchers(HttpMethod.POST, "/api/users");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().httpBasic()
             .and()
             .authorizeRequests().anyRequest().authenticated();
        //
        // http.authorizeRequests()
        //     .antMatchers(HttpMethod.POST, "/api/books").permitAll()
        //     .antMatchers(HttpMethod.POST, "/api/users").permitAll()
        //     .anyRequest().authenticated()
        //     .and().httpBasic();
    }
}

/*

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityJavaConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomAuthenticationProvider authenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().httpBasic()
                .and()
                .authorizeRequests().anyRequest().authenticated();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(HttpMethod.POST, "/api/books")
                .and().ignoring().antMatchers(HttpMethod.POST, "/api/users");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.authenticationProvider(authenticationProvider);
    }
}
 */