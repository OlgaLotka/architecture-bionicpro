package org.example.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.example.domain.Credentials;
import org.example.exception.BadAuthorizeException;
import org.example.domain.LoginResponseMessage;
import org.keycloak.authorization.client.AuthorizationDeniedException;
import org.keycloak.authorization.client.AuthzClient;
import org.keycloak.authorization.client.util.HttpResponseException;
import org.keycloak.representations.idm.authorization.AuthorizationResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthzClient authzClient;
    private final RedisTemplate<String, LoginResponseMessage> redisTemplate;

    public LoginResponseMessage login(Credentials credentials, Optional<String> sid) throws BadAuthorizeException {
        log.info("START login for user {}", credentials.getLogin());
        try {
            //redisTemplate.delete(credentials.getLogin());
            LoginResponseMessage responseMessage = null;
            if (sid.isPresent()){
                responseMessage = redisTemplate.opsForList().getLast(sid.get());
            }
            if (responseMessage != null) {
                if (new Date().before(responseMessage.getExpiresIn())) {
                    return responseMessage;
                } else if (new Date().before(responseMessage.getRefreshExpiresIn())){
                    return refreshToken(sid.get(),responseMessage.getTokenType(), responseMessage.getRefreshToken());
                }

            }
            val response = authzClient.authorization(credentials.getLogin(), credentials.getPassword())
                    .authorize();

            final var result = getLoginResponseMessage(response);
            log.info("FINISH login for user {} successfully", credentials.getLogin());

            redisTemplate.opsForList().leftPush(result.getSessionId(), result);
            return (LoginResponseMessage) result;
        } catch (AuthorizationDeniedException | HttpResponseException ex) {
            log.debug("Exception when login {}", credentials.getLogin(), ex);
            log.info("FINISH login for user {} is bad", credentials.getLogin());
            throw new BadAuthorizeException(ex);
        } catch (Exception ex) {
            log.error("Some error occurred during login");
            throw new BadAuthorizeException(ex);
        }
    }

    public LoginResponseMessage refreshToken(String email, String type, String refreshToken) {
        val response = authzClient.authorization(type+refreshToken)
                .authorize();
        val result = getLoginResponseMessage(response);
        log.info("FINISH login for user {} successfully", email);
        redisTemplate.opsForList().leftPush(email, result);
        return (LoginResponseMessage) result;
    }

    private static LoginResponseMessage getLoginResponseMessage(AuthorizationResponse response) {
        DecodedJWT jwt = JWT.decode(response.getToken());
        String sid = jwt.getClaim("sid").asString();
        return new LoginResponseMessage(sid, response.getToken(), response.getRefreshToken(), response.getTokenType(), getExpiresIn(response.getExpiresIn()), getExpiresIn(response.getRefreshExpiresIn()));
    }

    private static Date getExpiresIn(long response) {
        return new Date(System.currentTimeMillis() + (response*1000));
    }


}
