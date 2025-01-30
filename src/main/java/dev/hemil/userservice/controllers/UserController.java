package dev.hemil.userservice.controllers;

import dev.hemil.userservice.dtos.*;
import dev.hemil.userservice.dtos.ResponseStatus;
import dev.hemil.userservice.exceptions.UserNotFoundException;
import dev.hemil.userservice.models.Token;
import dev.hemil.userservice.models.User;
import dev.hemil.userservice.services.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }
    /* As we are using spring starter security dependency, all the endpoints are by default
    protected, but signup end-point should not be open to signup for new users, and so sir
    made a SecurityConfiguration file and permitted signup end-point.
    */
    @PostMapping("/signup")
    public SignUpResponseDto signup(@RequestBody SignUpRequestDto requestDto){
        User user = userService.signup(requestDto.getName(),
                requestDto.getEmail(),
                requestDto.getPassword()
        );

        SignUpResponseDto responseDto = new SignUpResponseDto();
        responseDto.setUser(user);
        responseDto.setResponseStatus(ResponseStatus.SUCCESS);

        return responseDto;
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto requestDto){
        Token token = userService.login(
                requestDto.getEmail(),
                requestDto.getPassword()
        );
        LoginResponseDto responseDto = new LoginResponseDto();
        responseDto.setToken(token);

        return responseDto;
    }

    @GetMapping("/validate/{token}")
    public UserDto validateToken(@PathVariable("token") String token){
        /* As this method is going to be used in other microservices as well, that's
        why we are not making it accept using requestBody and a dto.
        */
        User user = userService.validateToken(token);
        //This approach is just to make code look cleaner
        UserDto userDto = UserDto.fromUser(user);

        return userDto;
    }

    @PostMapping("/logout")
    public LogoutResponseDto logout(@RequestBody LogoutRequestDto requestDto){
        //Receive token from requestDto
        String token = requestDto.getToken();
        /* Logout method of UserService returns a String message that contains the name of
        the user who requested to be logged out, so at least logout method shows whom it
        logged out, by which user can get the confirmation.
        */
        String message = userService.logout(token);

        // Set the received parameters in responseDto and send it in response.
        LogoutResponseDto responseDto = new LogoutResponseDto();
        responseDto.setMessage(message);
        responseDto.setResponseStatus(ResponseStatus.SUCCESS);
        return responseDto;
    }

    @GetMapping("/{id}")
    public UserDto getUserDetails(@PathVariable("id") Long userId) throws UserNotFoundException {
        User user = userService.getUserDetails(userId);
        System.out.println("Received getUserDetails API request");
        return UserDto.fromUser(user);
    }
}
