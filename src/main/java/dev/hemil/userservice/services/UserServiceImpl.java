package dev.hemil.userservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.hemil.userservice.dtos.SendEmailEventDto;
import dev.hemil.userservice.dtos.UserDto;
import dev.hemil.userservice.exceptions.UserNotFoundException;
import dev.hemil.userservice.models.Token;
import dev.hemil.userservice.models.User;
import dev.hemil.userservice.repositories.TokenRepository;
import dev.hemil.userservice.repositories.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private TokenRepository tokenRepository;
    private KafkaTemplate<String, String> kafkaTemplate;
    private ObjectMapper objectMapper;
    public UserServiceImpl(UserRepository userRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           TokenRepository tokenRepository,
                           KafkaTemplate kafkaTemplate,
                           ObjectMapper objectMapper){
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public User signup(String name, String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user = null;
        if(optionalUser.isPresent()){
            //Redirect to login page
            login(email, password);
        }
        else {
            //Create a new user object
            user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setHashedPassword(bCryptPasswordEncoder.encode(password));
            user.setDeleted(false);

            // Save this user to UserRepository
            user = userRepository.save(user);
            SendEmailEventDto emailEventDto = new SendEmailEventDto();
            emailEventDto.setTo(email);
            emailEventDto.setFrom("hemilfichadia@gmail.com");
            emailEventDto.setSubject("welcome to Scaler");
            emailEventDto.setBody("Welcome to scaler We are happy to have you on our platform. All the best!!");

            try {
                kafkaTemplate.send(
                        "sendEmail",
                        objectMapper.writeValueAsString(emailEventDto)
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

        }
        return user;
    }

    @Override
    public Token login(String email, String password) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user = null;
        if(optionalUser.isEmpty()){
            //Redirect to signup method

        }
        else {
            user = optionalUser.get();

            if(!bCryptPasswordEncoder.matches(password, user.getHashedPassword())){
                return null;
            }
            //Generate token
            Token token = createNewToken(user);
            //Now store this token to the database.
            token = tokenRepository.save(token);
            return token;
        }
        return null;
    }

    private Token createNewToken(User user){
        /* This token code was picking up a lot number of lines, and from the Single
        Responsibility principle, we need to include it in separate method itself,
        so we created a separate method for it.
        */
        //Generate Token
        Token token = new Token();
        /* To generate a token, we are using a library named Apache commons lang 3; it
        provides us ways to generate different types random string.
        */
        token.setUser(user);
        token.setValue(RandomStringUtils.randomAlphanumeric(128));

        /* Sir suggests this; for this kind of functionality, we can take help from
        ChatGPT to generate this kind of code, so we don't need to memorize it.
        */
        //Get current date first
        LocalDate currentDate = LocalDate.now();
        //Get the date of 30 days later from the current date
        LocalDate thirtyDaysLater = currentDate.plusDays(30);

        //We need to convert it back to Date
        //First get the date at the start day for LocalDateTime
        LocalDateTime localDateTime = thirtyDaysLater.atStartOfDay();

        //Convert LocalDateTime to zoned date time using the system default time zone
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());

        //Convert zoned date time to Date
        Date expiryDate = Date.from(zonedDateTime.toInstant());

        //Set expiry date in token
        token.setExpiryAt(expiryDate);
        token.setDeleted(false);

        return token;
    }

    @Override
    public User validateToken(String token) {
        Optional<Token> tokenOptional = tokenRepository.findByValueAndDeletedAndExpiryAtGreaterThan(
                token,
                false,
                new Date()
        );

        if(tokenOptional.isEmpty()){
            //Throw some exception

        }
        return tokenOptional.get().getUser();
    }

    @Override
    public String logout(String token) {
        /* Here we have to check that if a token exists or not and also it's not deleted
        because here we recognize a user as logged out only if the token is deleted.
        */
        Optional<Token> optionalToken = tokenRepository.findByValueAndDeleted(token, false);
        if(optionalToken.isEmpty()){
            //Throw some exception

        }

        Token token1 = optionalToken.get();
        //Delete the token by setting the deleted attribute true
        token1.setDeleted(true);
        tokenRepository.save(token1);

        //Get user_id from token table
        Optional<Long> optionalUserId = tokenRepository.findUserIdByTokenValue(token);
        //Get user details from users table using user_id we received from token table
        Optional<User> userDetails = userRepository.findById(optionalUserId.get());

        String name = userDetails.get().getName();
        String message = "You have been logged out, see you soon " + name;

        return  message;
    }

    @Override
    public User getUserDetails(Long userId) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(userId);

        if(optionalUser.isEmpty()){
            throw new UserNotFoundException("User with id : " + userId + " not found");
        }

        return optionalUser.get();
    }
}
