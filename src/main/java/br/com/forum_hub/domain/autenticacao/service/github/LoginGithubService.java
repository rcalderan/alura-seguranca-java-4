package br.com.forum_hub.domain.autenticacao.service.github;


import br.com.forum_hub.domain.autenticacao.constants.OauthConstants;
import br.com.forum_hub.domain.autenticacao.interfaces.IOauthLogin;
import br.com.forum_hub.domain.autenticacao.model.DadosEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;


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
                OauthConstants.SCOPE, "read:user,user:email,public_repo"
        );

    }

    @Override
    public String authenticate(String code) {
        var response =  restClient.post()
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
                .body(Map.class);
        assert response != null;
        return response.get("access_token").toString();
    }

    public String getUser(String code){
        var token = authenticate(code);

        var headers = new HttpHeaders();
        headers.setBearerAuth(token);

        var response =  restClient.get()
                .uri("https://api.github.com/user")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);

        return response;
    }

    public String getEmail(String code){
        var token = authenticate(code);

        var headers = new HttpHeaders();
        headers.setBearerAuth(token);

        var response =  restClient.get()
                .uri("https://api.github.com/user/emails")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(DadosEmail[].class);

        var repositories =  restClient.get()
                .uri("https://api.github.com/user/repos")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);
        System.out.println(repositories);

        return Optional.ofNullable(response).stream()
                .flatMap(Arrays::stream)
                .filter(DadosEmail::primary)
                .map(DadosEmail::email)
                .findFirst()
                .orElse(null);

    }

    public String getRepositories(String code){
        var token = authenticate(code);

        var headers = new HttpHeaders();
        headers.setBearerAuth(token);

        var repositories =  restClient.get()
                .uri("https://api.github.com/user/repos")
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);
        System.out.println(repositories);

        return repositories;

//        return Optional.ofNullable(repositories).stream()
//                .flatMap(Arrays::stream)
//                .filter(DadosEmail::primary)
//                .map(DadosEmail::email)
//                .findFirst()
//                .orElse(null);

    }

}
