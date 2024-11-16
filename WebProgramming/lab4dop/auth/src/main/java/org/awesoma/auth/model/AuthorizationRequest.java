package org.awesoma.auth.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class AuthorizationRequest {
    @NotBlank
    private String token;

}
