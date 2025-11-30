package br.com.forum_hub.controller.oauth;

import br.com.forum_hub.domain.autenticacao.service.github.LoginGithubService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;



@RestController
@RequestMapping("/login/github")
public class GithubLoginController {
    private final String OAUTH_STATE_NAME = "oauth_state";
    @Autowired
    private LoginGithubService loginGithubService;

    @GetMapping
    public ResponseEntity<Void> githubRedirect(HttpSession session){

        var state = generateState();

        session.setAttribute(OAUTH_STATE_NAME, state);
        var url = loginGithubService.authorizeUrl(state);
        var headers = new HttpHeaders();
        headers.setLocation(URI.create(url));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }


    @GetMapping("/autorizado")
    public ResponseEntity<String> getToken(@RequestParam String code, @RequestParam String state, HttpSession session){
        String expectedState = (String) session.getAttribute(OAUTH_STATE_NAME);
        if (!state.equals(expectedState)) {
            System.out.println("State inválido! Possível ataque CSRF.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var token = loginGithubService.getAccessToken(code);
        return ResponseEntity.ok(token);
    }


    private String generateState(){
        return UUID.randomUUID().toString();
    }

}
