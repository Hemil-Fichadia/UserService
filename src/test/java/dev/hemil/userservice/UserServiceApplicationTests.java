package dev.hemil.userservice;

import dev.hemil.userservice.repositories.TokenRepository;
import dev.hemil.userservice.security.repositories.JpaRegisteredClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import java.util.Optional;
import java.util.UUID;

@SpringBootTest
class UserServiceApplicationTests {

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    JpaRegisteredClientRepository jpaRegisteredClientRepository;
    @Test
    void contextLoads() {
//        String tokenValue = "8bM8rdFYYbpHGOSZGCrsEDP7L6gvQweG4I5wF8F1vXZm25w3g0Wr0DBxmRtN529r5gHTUqNEVev25L7WtcdyC8ueBL2Wjt0HbuWceMshJlU3ig8wyR7hKn5KdCteTEUg";
//        Optional<Long> userId = tokenRepository.findUserIdByTokenValue(tokenValue);
//        System.out.println("user_id : "+userId.get());
    }

    @Test
    void addRegisteredClientToDB(){
        /* This was just meant to be executed once so that we can have one client registered
        in a database and to make it act like third party login verified client.
        */
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("oidc-client")
                .clientSecret("{noop}secret")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("https://oauth.pstmn.io/v1/callback")
                .postLogoutRedirectUri("https://oauth.pstmn.io/v1/callback")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("ADMIN")
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        jpaRegisteredClientRepository.save(registeredClient);
    }

}
