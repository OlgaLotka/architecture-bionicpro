package org.example.config;

import java.util.*;

import lombok.val;
import org.keycloak.OAuth2Constants;
import org.keycloak.adapters.springboot.KeycloakSpringBootProperties;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.authorization.client.AuthzClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true
)
public class SecurityConfig {

    /*@Autowired
    JwtAuthorizationTokenFilter jwtAuthorizationTokenFilter;*/

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        DefaultSecurityFilterChain build = http.authorizeHttpRequests(authorise ->
                        authorise
                                .requestMatchers("/auth/**", "/authorization/**")
                                .permitAll()
                                .anyRequest()
                                .permitAll()
                                )

                .csrf(AbstractHttpConfigurer::disable)
                //.oauth2ResourceServer((oauth2) -> oauth2.jwt())
                //.addFilterBefore(jwtAuthorizationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(authorise -> authorise.accessDeniedPage("/access-denied"))
                /*.oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()).authenticationEntryPoint((request, response, exception) -> {
                    System.out.println("Authentication failed"); // here I set a break point and I get the cause of exception
                    BearerTokenAuthenticationEntryPoint delegate = new BearerTokenAuthenticationEntryPoint();
                    delegate.commence(request, response, exception);}))*/
                //.oauth2Client(withDefaults())
                //.oauth2Login(withDefaults())
                .build();
        return build;
    }

    @ConfigurationProperties(prefix = "keycloak")
    @Bean
    public KeycloakSpringBootProperties keycloakSpringBootProperties(){
        return new KeycloakSpringBootProperties();
    }

    @Bean
    public Keycloak keycloak(KeycloakSpringBootProperties props) {
        return KeycloakBuilder.builder()
                .serverUrl(props.getAuthServerUrl())
                .realm(props.getRealm())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(props.getResource())
                //.username((String) props.getCredentials().get("username"))
                //.password((String) props.getCredentials().get("password"))
                .clientSecret((String) props.getCredentials().get("secret"))
                .build();
    }


    @Bean
    public AuthzClient keycloakAuthzClient(KeycloakSpringBootProperties props) {
        val config = new org.keycloak.authorization.client.Configuration(
                props.getAuthServerUrl(), props.getRealm(),
                props.getResource(), props.getCredentials(), null);

        return AuthzClient.create(config);
    }


    @Bean
    @SuppressWarnings("unchecked")
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {

        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
            authorities.forEach(authority -> {
                if (authority instanceof OidcUserAuthority oidcUserAuthority) {
                    OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();
                    Map<String, Object> realmAccess = userInfo.getClaim("realm_access");
                    Collection<String> realmRoles;
                    if (realmAccess != null
                            && (realmRoles = (Collection<String>) realmAccess.get("roles")) != null) {
                        realmRoles
                                .forEach(role -> mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role)));
                    }
                }
            });
            return mappedAuthorities;
        };
    }

   /* @Bean
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST,
            proxyMode = ScopedProxyMode.TARGET_CLASS)
    public AccessToken getAccessToken() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        KeycloakAuthenticationToken userPrincipal = (KeycloakAuthenticationToken) request.getUserPrincipal();
        SimpleKeycloakAccount userPrincipalDetails = (SimpleKeycloakAccount) userPrincipal.getDetails();
        return userPrincipalDetails
                .getKeycloakSecurityContext()
                .getToken();
    }*/


}