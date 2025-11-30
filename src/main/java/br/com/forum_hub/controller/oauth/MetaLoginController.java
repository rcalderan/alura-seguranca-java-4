package br.com.forum_hub.controller.oauth;

import br.com.forum_hub.domain.autenticacao.service.meta.LoginMetaService;
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

import static br.com.forum_hub.domain.autenticacao.constants.OauthConstants.OAUTH_STATE_NAME;

@RestController
@RequestMapping("login/meta")
public class MetaLoginController {

    @Autowired
    private LoginMetaService loginMetaService;

    @GetMapping
    public ResponseEntity<Void> metaRedirect(HttpSession session){

        var state = UUID.randomUUID().toString();
        session.setAttribute(OAUTH_STATE_NAME, state);

        var url = loginMetaService.authorizeUrl(state);
        var headers = new HttpHeaders();
        headers.setLocation(URI.create(url));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }


    @GetMapping("/authorized")
    public ResponseEntity<String> getMetaToken(@RequestParam String code, @RequestParam String state, HttpSession session){
        String expectedState = (String) session.getAttribute(OAUTH_STATE_NAME);
        if (!state.equals(expectedState)) {
            System.out.println("State inválido! Possível ataque CSRF.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var token = loginMetaService.authenticate(code);
        return ResponseEntity.ok(token);
    }

}
