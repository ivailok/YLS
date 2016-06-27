package ivailok.yls.models;

/**
 * Created by ivail on 7.6.2016 г..
 */
public class Credentials {
    private int expiresIn;
    private String tokenType;
    private String refreshToken;
    private String idToken;
    private String accessToken;

    public int getExpiresIn() {
        return expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
