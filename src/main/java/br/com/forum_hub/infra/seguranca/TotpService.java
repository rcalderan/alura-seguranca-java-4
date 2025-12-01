package br.com.forum_hub.infra.seguranca;


import br.com.forum_hub.domain.usuario.Usuario;
import com.atlassian.onetime.core.TOTPGenerator;
import com.atlassian.onetime.model.TOTPSecret;
import com.atlassian.onetime.service.RandomSecretProvider;
import org.springframework.stereotype.Service;

@Service
public class TotpService {
    public String secretGenerate(){
        return  new RandomSecretProvider().generateSecret().getBase32Encoded();
    }

    public String qrCodeGenerate(Usuario user){
        var issuer ="Forum Hub";
        return String.format(
                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                issuer, user.getUsername(), user.getSecret(), issuer
        );
    }

    public Boolean codeValidate(String code, Usuario usuario) {
        var decodedSecret = TOTPSecret.Companion.fromBase32EncodedString(usuario.getSecret());
        var appCode = new TOTPGenerator().generateCurrent(decodedSecret).getValue();
        return  appCode.equals(code);
    }
}
