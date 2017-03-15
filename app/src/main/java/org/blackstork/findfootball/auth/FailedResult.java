package org.blackstork.findfootball.auth;

/**
 * Created by WiskiW on 14.03.2017.
 */

public class FailedResult {

    /*
        FailedResult statusCode's:
            1 - unknown provider
            101 - GoogleApiClient connection failed
            12501 - при зкарытии окна выбора аккаунта google
            102 - какая-то ошибка при входе через Facebook
            103 - отмена логина через Facebook

            VK errors: https://vk.com/dev/errors


     */

    private int statusCode;
    private String message;
    private String provider;
    private int providerId;

    public FailedResult(int statusCode, String provider, int providerId) {
        this.statusCode = statusCode;
        this.provider = provider;
        this.providerId = providerId;
    }

    public FailedResult(int statusCode) {
        this.statusCode = statusCode;
    }

    public FailedResult statusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public FailedResult message(String message) {
        this.message = message;
        return this;
    }

    public FailedResult provider(String provider) {
        this.provider = provider;
        return this;
    }

    public FailedResult providerId(int providerId) {
        this.providerId = providerId;
        return this;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getProvider() {
        return provider;
    }

    public int getProviderId() {
        return providerId;
    }

    @Override
    public String toString() {
        return "Provider: " + provider +  " [code:" + statusCode + "] " + message;
    }
}
