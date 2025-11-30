package br.com.forum_hub.domain.autenticacao.service.google;

import br.com.forum_hub.domain.autenticacao.constants.OauthConstants;
import br.com.forum_hub.domain.autenticacao.interfaces.IOauthLogin;
import com.auth0.jwt.JWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class LoginGoogleService implements IOauthLogin {

    @Value("${forum.oauth.google.appId}")
    private String appId;
    @Value("${forum.oauth.google.secret}")
    private String secret;
    @Value("${forum.oauth.google.redirectUrl}")
    private String redirectUrl;

    private final RestClient restClient;

    public LoginGoogleService(RestClient.Builder clientBuilder) {
        this.restClient = clientBuilder.build();
    }

    @Override
    public String authorizeUrl(String state) {
        return String.format("%s?%s=%s&%s=%s&%s=%s&%s=%s&%s=%s",
                OauthConstants.GOOGLE_AUTH_URL,
                OauthConstants.CLIENT_ID, appId,
                OauthConstants.REDIRECT_URI, redirectUrl,
                OauthConstants.RESPONSE_TYPE, "code",
                //OauthConstants.SCOPE, "https://www.google.com/auth/userinfo.email",
                OauthConstants.SCOPE, URLEncoder.encode("openid email profile", StandardCharsets.UTF_8),
                OauthConstants.STATE, state
        );

    }

    @Override
    public String authenticate(String code) {
        var response =  restClient.post()
                .uri(OauthConstants.GOOGLE_TOKEN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        OauthConstants.CODE, code,
                        OauthConstants.CLIENT_ID, appId,
                        OauthConstants.CLIENT_SECRET, secret,
                        OauthConstants.REDIRECT_URI, redirectUrl,
                        OauthConstants.GRANT_TYPE, "authorization_code"
                ))
                .retrieve()
                .body(Map.class);
        return response.get("id_token").toString();

    }

    public String getEmail(String code){
        var token = authenticate(code);

        var decodedJWT = JWT.decode(token);
        System.out.println(decodedJWT.getClaims());
        return decodedJWT.getClaim("email").asString();
    }
}
