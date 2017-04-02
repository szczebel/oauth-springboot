package restserver;

import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.RestClientResponseException;

import static restserver.RestCall.restGet;
import static restserver.RestCall.restPostVoid;

public class Tester {

    private static final String ROOT = "http://localhost:9000/sso";

    public static void main(String[] args) {
        try {
            System.out.println("Login as existing user");
            oauthLogin("joe", "joe");
            //register and login with new user
            final String user = "fluffy";
            final String password = "joe";
            System.out.println("Register new user");
            register(user, password);
            System.out.println("Login as new user");
            oauthLogin(user, password);
        } catch (RestClientResponseException e) {
            System.err.println(e.getRawStatusCode());
            System.err.println(e.getStatusText());
            e.printStackTrace();
        }
    }

    private static void register(String user, String password) {
        restPostVoid(ROOT + "/register")
                .param("username", user)
                .param("password", password)
                .voidExecute();
    }

    private static void oauthLogin(String user, String password) {

        ResourceOwnerPasswordResourceDetails res = new ResourceOwnerPasswordResourceDetails();
        res.setClientId("SOMEAPP");
        res.setClientSecret("SECRET");
        res.setAccessTokenUri(ROOT + "/oauth/token");
        res.setUsername(user);
        res.setPassword(password);

        OAuth2RestTemplate t = new OAuth2RestTemplate(res);
        final OAuth2AccessToken accessToken = t.getAccessToken();
        System.out.println("Access token: " + accessToken);

        String p = restGet(ROOT + "/whois", String.class)
                .restTemplate(() -> t)
                .execute();
        System.out.println("User: " + p);
    }

}
