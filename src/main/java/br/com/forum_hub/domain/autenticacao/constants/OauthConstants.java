package br.com.forum_hub.domain.autenticacao.constants;


public class OauthConstants {
    private OauthConstants(){}


    public static final String OAUTH_STATE_NAME = "oauth_state";
    // parameters
    public static final String CLIENT_ID = "client_id";
    public static final String CLIENT_SECRET = "client_secret";
    public static final String REDIRECT_URI = "redirect_uri";
    public static final String CODE = "code";
    public static final String RESPONSE_TYPE = "response_type";
    public static final String STATE = "state";
    public static final String SCOPE = "scope";
    public static final String GRANT_TYPE = "grant_type";


    // URLs base
    public static final String META_AUTH_URL = "https://www.facebook.com/v20.0/dialog/oauth";
    public static final String META_TOKEN_URL = "https://graph.facebook.com/v20.0/oauth/access_token";

    public static final String GITHUB_AUTH_URL = "https://github.com/login/oauth/authorize";
    public static final String GITHUB_TOKEN_URL = "https://github.com/login/oauth/access_token";


    public static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/v2/auth";
    public static final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    public static final String GOOGLE_USER_URL = "https://www.googleapis.com/oauth2/v3/userinfo";

}
