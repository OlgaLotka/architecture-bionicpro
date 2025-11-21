package org.example;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.example.domain.LoginResponseMessage;
import org.example.domain.UserInfoDto;
import org.example.exception.BadAuthorizeException;
import org.example.token.AuthService;
import org.example.domain.Credentials;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

import static org.example.token.Util.decodeBasicAuthHeader;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true",     methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class HelloController {

    private final AuthService authService;

    @PostMapping("/auth")
    @PreAuthorize("permitAll()")
    public ResponseEntity<LoginResponseMessage> getMe(@RequestHeader Map<String, String> headers) throws BadAuthorizeException {
        Credentials authorization = decodeBasicAuthHeader(headers.get("authorization"));
        val responseMessage = authService.login(authorization, Optional.empty());
        return ResponseEntity.status(HttpStatus.OK)
                .body(responseMessage);
    }

    @GetMapping("/me")
    public UserInfoDto getGretting(JwtAuthenticationToken auth) {
        return new UserInfoDto(
                auth.getToken().getClaimAsString(StandardClaimNames.PREFERRED_USERNAME),
                auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
    }



    @GetMapping("/report")
    //@PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> getReport(@RequestHeader Map<String, String> headers) throws BadAuthorizeException {

        return ResponseEntity.ok().body(null);
    }

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity handleAuthNotFoundException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }


    @ExceptionHandler(BadAuthorizeException.class)
    public ResponseEntity handleBadAuthorizeException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
