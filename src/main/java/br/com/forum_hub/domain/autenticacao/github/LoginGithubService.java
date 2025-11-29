package br.com.forum_hub.domain.autenticacao.github;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.util.Map;


@Service
public class LoginGithubService {
    @Value("${forum.security.github.appId}")
    private String appId;
    @Value("${forum.security.github.secret}")
    private String secret;
    @Value("${forum.security.github.redirectUrl}")
    private String redirectUrl;

    private final RestClient restClient ;

    public LoginGithubService(RestClient.Builder clientBuilder) {
        this.restClient = clientBuilder.build();
    }


    public String gitHubUrl(){
        return "https://github.com/login/oauth/authorize"+
                "?client_id="+this.appId+
                "&redirect="+this.redirectUrl+
                "&scope=read:user,user:email";
    }

    public String getToken(String code) {
        var uri = URI.create("https://github.com/login/oauth/access_token"+
                "?client_id="+this.appId+
                "&redirect="+this.redirectUrl+
                "&scope=read:user,user:email");
        return restClient.post()
                .uri("https://github.com/login/oauth/access_token")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "code", code,
                        "client_id",appId,
                        "client_secret", secret,
                        "redirect_uri",redirectUrl))
                .retrieve()
                .body(String.class);
    }
}
