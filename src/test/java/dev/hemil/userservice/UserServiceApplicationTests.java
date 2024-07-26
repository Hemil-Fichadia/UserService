package dev.hemil.userservice;

import dev.hemil.userservice.repositories.TokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
class UserServiceApplicationTests {

    @Autowired
    TokenRepository tokenRepository;
    @Test
    void contextLoads() {
        String tokenValue = "8bM8rdFYYbpHGOSZGCrsEDP7L6gvQweG4I5wF8F1vXZm25w3g0Wr0DBxmRtN529r5gHTUqNEVev25L7WtcdyC8ueBL2Wjt0HbuWceMshJlU3ig8wyR7hKn5KdCteTEUg";
        Optional<Long> userId = tokenRepository.findUserIdByTokenValue(tokenValue);
        System.out.println("user_id : "+userId.get());
    }

}
