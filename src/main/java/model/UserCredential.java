package model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCredential {
    private User user;
    private final String accessToken;
    private String refreshToken;
}
