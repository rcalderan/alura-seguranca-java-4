package br.com.forum_hub.controller;

import br.com.forum_hub.domain.autenticacao.model.Dados2Fa;
import br.com.forum_hub.domain.autenticacao.model.DadosLogin;
import br.com.forum_hub.domain.autenticacao.model.DadosRefreshToken;
import br.com.forum_hub.domain.autenticacao.model.DadosToken;
import br.com.forum_hub.domain.autenticacao.service.TokenService;
import br.com.forum_hub.domain.usuario.Usuario;
import br.com.forum_hub.domain.usuario.UsuarioRepository;
import br.com.forum_hub.infra.seguranca.TotpService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;
    private final TotpService totpService;

    public AutenticacaoController(AuthenticationManager authenticationManager, TokenService tokenService, UsuarioRepository usuarioRepository, TotpService totpService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.usuarioRepository = usuarioRepository;
        this.totpService = totpService;
    }

    @PostMapping("/login")
    public ResponseEntity<DadosToken> efetuarLogin(@Valid @RequestBody DadosLogin dados){
        var autenticationToken = new UsernamePasswordAuthenticationToken(dados.email(), dados.senha());
        var authentication = authenticationManager.authenticate(autenticationToken);

        var user = (Usuario)authentication.getPrincipal();
        if(user.is2FAActive()){
            return ResponseEntity.ok(new DadosToken(null,null, true));
        }

        String tokenAcesso = tokenService.gerarToken((Usuario) authentication.getPrincipal());
        String refreshToken = tokenService.gerarRefreshToken((Usuario) authentication.getPrincipal());

        return ResponseEntity.ok(new DadosToken(tokenAcesso, refreshToken, false));
    }


    @PostMapping("/verificar-2fa")
    public ResponseEntity<DadosToken> verificar2FA(@Valid @RequestBody Dados2Fa dados) {
        var user = usuarioRepository.findByNomeUsuarioIgnoreCaseAndVerificadoTrueAndAtivoTrue(dados.email())
                .orElseThrow();
        var isValid = totpService.codeValidate(dados.code(), user);
        if(!isValid){
            throw new BadCredentialsException("Codigo inválido");
        }

        String tokenAcesso = tokenService.gerarToken(user);
        String refreshToken = tokenService.gerarRefreshToken(user);

        return ResponseEntity.ok(new DadosToken(tokenAcesso, refreshToken, false));
    }

    @PostMapping("/atualizar-token")
    public ResponseEntity<DadosToken> atualizarToken(@Valid @RequestBody DadosRefreshToken dados){
        var refreshToken = dados.refreshToken();
        Long idUsuario = Long.valueOf(tokenService.verificarToken(refreshToken));
        var usuario = usuarioRepository.findById(idUsuario).orElseThrow();

        String tokenAcesso = tokenService.gerarToken(usuario);
        String tokenAtualizacao = tokenService.gerarRefreshToken(usuario);

        return ResponseEntity.ok(new DadosToken(tokenAcesso, tokenAtualizacao, false));
    }
}
