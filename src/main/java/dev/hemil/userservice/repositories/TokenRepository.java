package dev.hemil.userservice.repositories;

import dev.hemil.userservice.models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Override
    Token save(Token token);


    Optional<Token> findByValueAndDeleted(String token, boolean deleted);

    /* This query returns user_id which matches the value of given token in the database,
    and for that I have used the HQL (Hibernate Query Language) and which works on the basis
    of relation among entities or class, so we have to provide the reference of entity to
    which the given attribute belongs to in order to fetch it from the database.
    */
    @Query("SELECT t.user.id FROM Token t WHERE t.value = :tokenValue")
    Optional<Long> findUserIdByTokenValue(@Param("tokenValue") String token);

    Optional<Token> findByValueAndDeletedAndExpiryAtGreaterThan(String token, Boolean deleted, Date expiryAt);
}
