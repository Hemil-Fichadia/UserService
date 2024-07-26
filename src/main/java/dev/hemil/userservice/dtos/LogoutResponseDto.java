package dev.hemil.userservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LogoutResponseDto {
    private String message;
    private ResponseStatus responseStatus;
}
