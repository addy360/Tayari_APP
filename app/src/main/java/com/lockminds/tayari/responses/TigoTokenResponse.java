package com.lockminds.tayari.responses;

public class TigoTokenResponse {
    String accessToken, token_type;
    int expireIn;

    public TigoTokenResponse(String accessToken, String token_type, int expireIn) {
        this.accessToken = accessToken;
        this.token_type = token_type;
        this.expireIn = expireIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public int getExpireIn() {
        return expireIn;
    }

    public void setExpireIn(int expireIn) {
        this.expireIn = expireIn;
    }
}

