package br.com.forum_hub.domain.autenticacao.service.github;


import br.com.forum_hub.domain.autenticacao.constants.OauthConstants;
import br.com.forum_hub.domain.autenticacao.interfaces.IOauthLogin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Map;


@Service
public class LoginGithubService implements IOauthLogin{
    @Value("${forum.oauth.github.appId}")
    private String appId;
    @Value("${forum.oauth.github.secret}")
    private String secret;
    @Value("${forum.oauth.github.redirectUrl}")
    private String redirectUrl;

    private final RestClient restClient;

    public LoginGithubService(RestClient.Builder clientBuilder) {
        this.restClient = clientBuilder.build();
    }


    @Override
    public String authorizeUrl(String state){
        return String.format("%s?%s=%s&%s=%s&%s=%s&%s=%s",
                OauthConstants.GITHUB_AUTH_URL,
                OauthConstants.CLIENT_ID, appId,
                OauthConstants.REDIRECT_URI, redirectUrl,
                OauthConstants.STATE, state,
                OauthConstants.SCOPE, "read:user,user:email"
        );

    }

    @Override
    public String getAccessToken(String code) {
        return restClient.post()
                .uri(OauthConstants.GITHUB_TOKEN_URL)
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
