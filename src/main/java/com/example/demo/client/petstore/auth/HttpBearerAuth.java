package com.example.demo.client.petstore.auth;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", date = "2026-02-05T20:16:37.023630+08:00[Asia/Shanghai]")
public class HttpBearerAuth implements Authentication {
    private final String scheme;
    private String bearerToken;

    public HttpBearerAuth(String scheme) {
        this.scheme = scheme;
    }

    public String getBearerToken() {
        return bearerToken;
    }

    public void setBearerToken(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    @Override
    public void applyToParams(MultiValueMap<String, String> queryParams, HttpHeaders headerParams, MultiValueMap<String, String> cookieParams) {
        if (bearerToken == null) {
            return;
        }
        headerParams.add(HttpHeaders.AUTHORIZATION, (scheme != null ? upperCaseBearer(scheme) + " " : "") + bearerToken);
    }

    private static String upperCaseBearer(String scheme) {
        return ("bearer".equalsIgnoreCase(scheme)) ? "Bearer" : scheme;
    }

}
