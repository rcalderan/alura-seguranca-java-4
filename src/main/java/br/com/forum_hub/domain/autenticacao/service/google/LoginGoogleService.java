package br.com.forum_hub.domain.autenticacao.service.google;

import br.com.forum_hub.domain.autenticacao.constants.OauthConstants;
import br.com.forum_hub.domain.autenticacao.interfaces.IOauthLogin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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
                "https://accounts.google.com/o/oauth2/v2/auth",
                OauthConstants.CLIENT_ID, appId,
                OauthConstants.REDIRECT_URI, redirectUrl,
                OauthConstants.RESPONSE_TYPE, "code",
                OauthConstants.SCOPE, "openid email profile",
                OauthConstants.STATE, state
        );

    }

    @Override
    public String getAccessToken(String code) {
        return restClient.post()
                .uri("https://oauth2.googleapis.com/token")
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
                .body(String.class);

    }
}
