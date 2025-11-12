package org.example.token;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.example.domain.Credentials;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

public class Util {

    public static Credentials decodeBasicAuthHeader(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            return null; // Not a valid Basic Auth header
        }

        String base64Credentials = authorizationHeader.substring("Basic ".length());
        byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(decodedBytes, StandardCharsets.UTF_8);

        String[] parts = credentials.split(":", 2); // Split only on the first colon
        if (parts.length == 2) {
            return new Credentials(parts[0], parts[1]); // parts[0] is username, parts[1] is password
        } else {
            return null; // Malformed credentials
        }
    }

    public static Optional<String> getSessionId(HttpServletRequest request) {
        Optional<Cookie> sessionId = Arrays.stream(request.getCookies())
                .filter(e -> Objects.equals(e.getName(), "session_id"))
                .findFirst();
        if (sessionId.isPresent()){
            return sessionId
                    .get().getValue().describeConstable();
        }
        return Optional.empty();
    }

    public static Credentials getCredentials(HttpServletRequest request) {
        return decodeBasicAuthHeader(String.valueOf(request.getHeaders("authorization").nextElement()));
    }


}
