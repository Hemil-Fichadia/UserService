package dev.hemil.userservice.dtos;

import dev.hemil.userservice.models.Role;
import dev.hemil.userservice.models.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDto {
    private String name;
    private String email;
    private List<Role> roles;

    /* Where ever you observe this kind of method being used just to set some attributes,
    it's actually reducing a lot of work where ever this kind of response is supposed to be
    sent, and it looks a lot cleaner than setting all those attributes inside the controller
    where we are just ensuring that response is generated, but a programmer equipped well with
    fundamentals, looks on to make it cleaner and cleaner that each responsibility is visible,
    yet they don't repeat or overlap
    */
    public static UserDto fromUser(User user){
        UserDto userDto = new UserDto();
        userDto.setName(user.getName());
        userDto.setEmail((user.getEmail()));

        return userDto;
    }
}
