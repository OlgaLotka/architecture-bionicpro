package org.example.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.domain.Credentials;
import org.example.domain.LoginResponseMessage;
import org.example.exception.BadAuthorizeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.Random;

import static org.example.token.Util.getCredentials;
import static org.example.token.Util.getSessionId;


@Component
public class JwtAuthorizationTokenFilter extends OncePerRequestFilter {


    @Autowired
    private AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        Credentials authorization = getCredentials(request);
        Optional<String> sessionId = getSessionId(request, "session_id");
        Optional<String> codeVerifier = getSessionId(request, "code_verifier");

        LoginResponseMessage responseMessage = null;

        responseMessage = authService.login(authorization, sessionId);

        DecodedJWT jwt = JWT.decode(responseMessage.getToken());
        String name = jwt.getClaim("name").asString();
        //Токен теперь только наш
        //response.addHeader(HttpHeaders.AUTHORIZATION, responseMessage.getToken());
        response.addHeader("Set-Cookie", getCookie(responseMessage, codeVerifier));
        response.addHeader("name", name);
        response.addHeader("Cache-Control", "private, max-age=60");
        chain.doFilter(request, response);
    }

    private static String getCookie(LoginResponseMessage responseMessage, Optional<String> codeVerifier) {
        return /*"HttpOnly;"
                + */"session_id=" + responseMessage.getSessionId() +
                ";code_verifier=" + codeVerifier.orElse("123") +
                ";Max-Age=360" +
                 ";";
    }


}
