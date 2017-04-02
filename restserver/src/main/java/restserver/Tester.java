package restserver;

import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.RestClientResponseException;

import static restserver.RestCall.restGet;

public class Tester {

    private static final String ROOT = "http://localhost:9000/sso";

    public static void main(String[] args) {
        try {
            withUserPass();
        } catch (RestClientResponseException e) {
            System.err.println(e.getRawStatusCode());
            System.err.println(e.getStatusText());
            e.printStackTrace();
        }
    }

    private static void withUserPass() {

        ResourceOwnerPasswordResourceDetails res = new ResourceOwnerPasswordResourceDetails();
        res.setClientId("SOMEAPP");
        res.setClientSecret("SECRET");
        res.setAccessTokenUri(ROOT + "/oauth/token");
        res.setUsername("joe");
        res.setPassword("joe");

        dance(ROOT, res);
    }

    private static void dance(String root, OAuth2ProtectedResourceDetails res) {
        OAuth2RestTemplate t = new OAuth2RestTemplate(res);
        final OAuth2AccessToken accessToken = t.getAccessToken();
        System.out.println(accessToken);

        String p = restGet(root + "/whois", String.class)
                .restTemplate(() -> t)
                .execute();
        System.out.println(p);
    }
}
