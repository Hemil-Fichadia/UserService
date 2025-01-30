package dev.hemil.userservice.services;

import dev.hemil.userservice.dtos.UserDto;
import dev.hemil.userservice.exceptions.UserNotFoundException;
import dev.hemil.userservice.models.Token;
import dev.hemil.userservice.models.User;

public interface UserService {
    User signup(String name, String email, String password);

    Token login(String email, String password);

    User validateToken(String token);

    String logout(String token);

    User getUserDetails(Long userId) throws UserNotFoundException;
}
