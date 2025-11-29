package br.com.forum_hub.domain.autenticacao.interfaces;

public interface IOauthLogin {

    String authorizeUrl(String state);

    String getAccessToken(String code);

}
