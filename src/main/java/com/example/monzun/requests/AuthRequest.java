package com.example.monzun.requests;

import com.example.monzun.validation.rules.ExistsUserEmail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class AuthRequest {
    @NotNull(message = "Email is required")
    @Email(message = "Email must be valid")
    @ExistsUserEmail
    @ApiModelProperty(required = true)
    private String email;
    @NotNull(message = "Password is required")
    @ApiModelProperty(required = true)
    private String password;
}