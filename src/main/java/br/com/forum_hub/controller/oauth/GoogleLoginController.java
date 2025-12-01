package br.com.forum_hub.controller.oauth;

import br.com.forum_hub.domain.autenticacao.model.DadosToken;
import br.com.forum_hub.domain.autenticacao.service.TokenService;
import br.com.forum_hub.domain.autenticacao.service.google.LoginGoogleService;
import br.com.forum_hub.domain.usuario.Usuario;
import br.com.forum_hub.domain.usuario.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/login/google")
public class GoogleLoginController {

    private final String OAUTH_STATE_NAME = "oauth_state";
    @Autowired
    private LoginGoogleService loginGoogleService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TokenService tokenService;

    @GetMapping
    public ResponseEntity<Void> googleRedirect(HttpSession session){

        var state = UUID.randomUUID().toString();

        session.setAttribute(OAUTH_STATE_NAME, state);
        var url = loginGoogleService.authorizeUrl(state);
        var headers = new HttpHeaders();
        headers.setLocation(URI.create(url));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }


    @GetMapping("/authorized")
    public ResponseEntity<DadosToken> authenticateUser(@RequestParam String code, @RequestParam String state, HttpSession session){
        String expectedState = (String) session.getAttribute(OAUTH_STATE_NAME);
        if (!state.equals(expectedState)) {
            System.out.println("State inválido! Possível ataque CSRF.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        var email = loginGoogleService.getEmail(code);

        Usuario usuario = usuarioRepository.findByEmailIgnoreCaseAndVerificadoTrue(email).orElseThrow();

        var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String tokenAcesso = tokenService.gerarToken((Usuario) authentication.getPrincipal());
        String refreshToken = tokenService.gerarRefreshToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new DadosToken(tokenAcesso, refreshToken, false));
    }
}
