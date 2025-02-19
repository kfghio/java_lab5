package itmo.dev.api_gateway.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String username;
    private String password;
}
