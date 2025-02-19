package itmo.dev.api_gateway.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String username;
    private String password;
}
