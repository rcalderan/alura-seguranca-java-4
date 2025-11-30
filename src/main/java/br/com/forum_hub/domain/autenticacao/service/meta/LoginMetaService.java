package br.com.forum_hub.domain.autenticacao.service.meta;

import br.com.forum_hub.domain.autenticacao.constants.OauthConstants;
import br.com.forum_hub.domain.autenticacao.interfaces.IOauthLogin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class LoginMetaService implements IOauthLogin {

    @Value("${forum.oauth.meta.appId}")
    private String appId;
    @Value("${forum.oauth.meta.secret}")
    private String secret;
    @Value("${forum.oauth.meta.redirectUrl}")
    private String redirectUrl;

    private final RestClient restClient;

    public LoginMetaService(RestClient.Builder restBuilder) {
        this.restClient = restBuilder.build();
    }

    @Override
    public String authorizeUrl(String state) {
        return String.format("%s?%s=%s&%s=%s&%s=%s&%s=%s",
                OauthConstants.META_AUTH_URL,
                OauthConstants.CLIENT_ID, appId,
                OauthConstants.REDIRECT_URI, redirectUrl,
                OauthConstants.STATE, state,
                OauthConstants.SCOPE, "email,public_profile"
        );
    }

    @Override
    public String authenticate(String code) {
        return restClient.post()
                .uri(OauthConstants.META_TOKEN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        OauthConstants.CODE, code,
                        OauthConstants.CLIENT_ID, appId,
                        OauthConstants.CLIENT_SECRET, secret,
                        OauthConstants.REDIRECT_URI, redirectUrl
                ))
                .retrieve()
                .body(String.class);

    }
}
