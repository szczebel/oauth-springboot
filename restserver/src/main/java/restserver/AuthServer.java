package restserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Collections.singleton;

@SpringBootApplication
@EnableAuthorizationServer
@RestController
public class AuthServer {

    public static void main(String[] args) {
        SpringApplication.run(AuthServer.class);
    }

    @GetMapping("/whois")
    Principal whois(Principal principal) {
        return principal;
    }

    @Configuration
    protected static class MvcConfig extends WebMvcConfigurerAdapter {
        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/registration").setViewName("registrationForm");
        }
    }

    @Controller
    protected static class Registration {

        @Autowired
        UserService userService;

        @PostMapping("/register")
        String register(@RequestParam String username, @RequestParam String password) {
            userService.create(username, password);
            final String redirect_uri = "http://localhost:9001/ui";//should be a param
            return "redirect:" + redirect_uri;
        }
    }

    @Configuration
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class SecurityConfig extends WebSecurityConfigurerAdapter {

        @Autowired
        UserService userService;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.httpBasic();
        }


        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(userService);
            auth.inMemoryAuthentication()
                    .withUser("joe").password("joe").roles("SUPERVISOR");
        }

        //this is used by oauth, otherwise users are not accessible
        @Override
        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
    }

    @EnableResourceServer
    @Configuration
    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
    protected static class ResourceServerConfig extends ResourceServerConfigurerAdapter {

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
                    .antMatchers("/registrationForm.html").permitAll()
                    .antMatchers("/register").permitAll()
                    .antMatchers("/login").permitAll()
                    .anyRequest().authenticated();
        }
    }

    @Component
    protected static class UserService implements UserDetailsService {

        Map<String, UserDetails> users = new ConcurrentHashMap<>();

        @Override
        public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
            if (!users.containsKey(s)) throw new UsernameNotFoundException(s);
            return users.get(s);
        }

        void create(String username, String password) {
            users.putIfAbsent(username, new User(
                    username, password,
                    singleton(new SimpleGrantedAuthority("ROLE_USER"))));
        }
    }
}
