package uiserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@SpringBootApplication
@EnableOAuth2Sso
@RestController
public class UiServer {


    public static void main(String[] args) {
        SpringApplication.run(UiServer.class);
    }

    @GetMapping("/")
    String helloWorld(Principal principal) {
        return "Yay, auth server provided identity, you are " + principal;
    }

//    @Configuration
//    @Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER)
//    protected static class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
////            http.
//        }
//    }
}

// Invalid CSRF token found for http://localhost:9000/sso/oauth/access_token
//fix: disable csrf

// Basic Authentication Authorization header found for user 'SOMEAPP'
// User 'SOMEAPP' not found
//fix: change to formLogin(), to remove the Basic AA filter

///whois at position 9 of 11 in additional filter chain; firing Filter: 'AnonymousAuthenticationFilter'
//        2017-04-01 20:45:31.709 DEBUG 1048 --- [nio-9000-exec-9] o.s.s.w.a.AnonymousAuthenticationFilter  : Populated SecurityContextHolder with anonymous token
//fix: @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER) on my SecurityConfig

//http://localhost:9000/sso/login now returns 'unauthorized' XML
//fix: ("/login").permitAll() in ResourceServerConfig (did not work when added in SecurityConfig)

//sso:404 Did not find handler method for [/login]
//fix: .formLogin() in ResourceServerConfig

// /sso/login loop, can't login. DefaultLoginPageGeneratingFilter handles /login submission
// before it reaches SecurityFilter/LoginController (or whichever entity which authenticates and resumes the flow)?
//fix: remove redundant .formLogin() from SecurityConfig

// /sso/login is now Forbidden:403 (...Pre-authenticated entry point called. Rejecting access)
//fix: @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER) on ResourceServerConfig, crsf().disable() moved to ReSeCo, and remove SecurityConfig

//Yay, it works... wait. There was no 'confirm_access' gui, and sso login was with Basic Authentication...

//But, no form login on /sso/login (Basic Authentication)
//fix: SecurityConfig back in, but retain ACCESS_OVERRIDE_ORDER on ReSoCo

//------------------------

//fixed accessTokenUri: /oauth/access_token -> /oauth/token. Yay, it works!





